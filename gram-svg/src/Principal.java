import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.StringValueExp;
import javax.swing.text.Position;

import java.util.regex.Matcher;
import java.math.*;

public class Principal {
    public static void main(String[] args) throws Exception {
        

        Path path = Paths.get("input.txt");
        List<String> lista = Files.readAllLines(path, StandardCharsets.UTF_8);

        if(!validaLista(lista)){
            System.out.println("Arquivo input.txt inválido");
            return;
        }

        int iterations = Integer.parseInt(lista.get(0).substring(3).strip());
        String axion = lista.get(1).substring(3).strip();
        int girar = Integer.parseInt(lista.get(2).substring(3).strip());

        Pattern pattern = Pattern.compile("p\\d : *(F|f|\\+|-|\\[|\\]) *-> *([F|f|\\+|\\-|\\[|\\]]+)");

        Map<String, String> rules = new HashMap<String, String>();
        for(String line : lista){
            Matcher m = pattern.matcher(line);
            if(m.find()){
                rules.put(m.group(1), m.group(2));
            }
        }


        StringBuilder result= new StringBuilder();
        result.append(axion);
        System.out.println("n = 0: " + axion);
        for(int i = 1; i <= iterations; i++)
        {
            String currentString = result.toString();
            result.setLength(0);
            for (char c : currentString.toCharArray())
            {
                if(rules.containsKey(String.valueOf(c)))
                    result.append(rules.get(String.valueOf(c)));
                else{
                    result.append(String.valueOf(c));
                }
            }
            System.out.println("n = " + i + ": " + result.toString());
        }

        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> angulo = new ArrayList<>();

        x.add((double) 40);
        y.add((double) 50);
        angulo.add(Math.toRadians(-60));

        double step = 0.5;

        x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
        y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));

        List<String> linhas = new ArrayList<>();

        for(char c : result.toString().toCharArray()){
            if(c == 'F' || c == 'f'){
                StringBuilder linha = new StringBuilder();
                linha.append("<line x1=\"");
                linha.append(x.get(x.size()-2));
                linha.append("%\" y1=\"");
                linha.append(y.get(y.size()-2));

                linha.append("%\" x2=\"");
                linha.append(x.get(x.size()-1));
                linha.append("%\" y2=\"");
                linha.append(y.get(y.size()-1));
                linha.append("%\"/>");


                linhas.add(linha.toString());
            }
            if(c == 'F' || c == 'f'){
                x.remove(x.size()-2);
                y.remove(y.size()-2);
                x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
                y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));
            }
            else if(c == '+' || c == '-'){
                if(c == '+')
                    angulo.add(angulo.get(angulo.size() - 1) - Math.toRadians(girar));
                    
                else if(c == '-')
                    angulo.add(angulo.get(angulo.size() - 1) + Math.toRadians(girar));

                angulo.remove(angulo.size() - 2);
                
                x.remove(x.size()-1);
                y.remove(y.size()-1);

                x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
                y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));
            }
            else if(c == '[' || c == ']'){
                if(c == '['){
                    x.add(x.get(x.size() - 2));
                    x.add(x.get(x.size() - 2));
                    y.add(y.get(y.size() - 2));
                    y.add(y.get(y.size() - 2));
                    angulo.add(angulo.get(angulo.size() - 1));
                }

                if(c == ']'){
                    x.remove(x.size() - 1);
                    x.remove(x.size() - 1);
                    y.remove(y.size() - 1);
                    y.remove(y.size() - 1);
                    angulo.remove(angulo.size() - 1);
                }
            }
        }


        criaHtml(linhas);
        
    }

    public static boolean validaLista(List<String> list) {
        Pattern pattern = Pattern.compile("^(p\\d+|Σ|n|ω|δ)\\s:");

        if(list.size() < 4)
            return false;

        for(String line : list){
            if(!pattern.matcher(line).find())
                return false;
        }
        return true;
    }

    public static void criaHtml(List<String> conteudo) throws Exception{
        Path path = Paths.get("svg.html");
        StringBuilder build = new StringBuilder();
        build.append("<html><body style=\"background-color:gray;\"><div style=\"position: fixed; top: 0; z-index: 1000;\"><input type=\"range\" style=\" width: 500px\" min=\"100\" max=\"500\" value=\"100\" class=\"slider\" id=\"zoomRange\"><span id=\"zoomValue\">100%</span></div><svg id=\"svgZoom\" viewBox=\"0 0 3000 3000\" preserveAspectRatio=\"xMidYMid meet\" style=\"stroke:rgb(4, 205, 255);stroke-width:2\">");
        for(String s : conteudo){
            build.append(s);
        }
        build.append("</svg></body><script>const slider = document.getElementById(\"zoomRange\");const svgZoom = document.getElementById(\"svgZoom\");const zoomValue = document.getElementById(\"zoomValue\");slider.oninput = function() {zoomValue.innerText = `${this.value}%`;svgZoom.style.transform = `scale(${this.value / 100})`;}</script></html>");
        byte[] bytes = build.toString().getBytes();

        Files.write(path, bytes);
    }
}
