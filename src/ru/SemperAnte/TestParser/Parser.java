package ru.SemperAnte.TestParser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser
{
    String parseFromString(String str, String from, String to) throws UnsupportedEncodingException
    {
        String s = new String(str.getBytes(from), to);
        ArrayList strings = new ArrayList(Arrays.asList(s.split("\n")));
        return parseText(strings);
    }

    private String parseText(List<String> lines)
    {
            for (int i = 0; i < lines.size() - 1; ++i)
            {
                String ln = lines.get(i);
                if (ln.endsWith(" "))
                {
                    ln += lines.get(i + 1);
                    lines.set(i, ln);
                    lines.remove(i + 1);
                    continue;
                }
                if ((ln.startsWith(" ") || ln.startsWith("  ")) && !ln.startsWith("   "))
                {
                    if (i != 0)
                    {
                        String l = lines.get(i - 1);
                        l += " " + replaceBigSpaces(ln);
                        lines.set(i - 1, l);
                        lines.remove(i);
                        --i;
                        continue;
                    }
                }
                if (ln.startsWith("."))
                {
                    String l = lines.get(i - 1);
                    l += ln;
                    lines.set(i - 1, l);
                    lines.remove(i);
                    --i;
                    continue;
                }
                ln = ln.trim();
                if (ln.endsWith(",") || ln.endsWith(")"))
                {

                    ln += " " + lines.get(i + 1);
                    lines.set(i, ln);
                    lines.remove(i + 1);
                    --i;
                    continue;
                }
                if (ln.endsWith("-") || ln.endsWith("—") || ln.endsWith("\u00AD") || ln.endsWith("\u00AD"))
                {
                    ln = ln.substring(0, ln.length() - 1);
                    ln += lines.get(i + 1);
                    lines.set(i, ln);
                    lines.remove(i + 1);
                    --i;
                }

                if (
                        ln.startsWith("-") ||
                                ln.startsWith("—") ||
                                ln.startsWith("\u00AD") ||
                                ln.startsWith("\u00AD") ||
                                ln.startsWith(",") ||
                                ln.startsWith(".") ||
                                ln.startsWith("(") ||
                                ln.startsWith("«") || ln.startsWith("\""))
                {
                    String l = lines.get(i - 1);
                    l += " " + replaceBigSpaces(ln);
                    lines.set(i - 1, l);
                    lines.remove(i);
                    --i;
                }
                if (ln.split(" ").length == 1 && !ln.isEmpty())
                {
                    ln += " " + lines.get(i + 1);
                    lines.set(i, ln);
                    lines.remove(i + 1);
                    --i;
                }
                char[] str = ln.toCharArray();
                if (str.length != 0)
                {
                    if (Character.isLowerCase(str[0]) && !Character.isDigit(str[0]) && i != 0)
                    {
                        String l = lines.get(i - 1);
                        l += " " + replaceBigSpaces(ln);
                        lines.set(i - 1, l);
                        lines.remove(i);
                        --i;
                        continue;
                    }
                }

//                if(!isNormalEnd(ln))
//                {
//                    StringBuilder sb = new StringBuilder(ln);
//                    for(int j = i+1; j<lines.size(); ++j)
//                    {
//                        String ln2 = lines.get(j);
//                        sb.append(" ").append(ln2);
//                        lines.remove(j);
//                        if(isNormalEnd(ln2))
//                            break;
//                    }
//                    lines.set(i, sb.toString());
//                }
            }

        List<Integer> toAdd = new ArrayList<>();
        for (int i = 0; i < lines.size(); ++i)
        {
            String ln = lines.get(i);
            String[] s = ln.split("\\;");
            boolean have = false;
            lines.set(i, s[0] + ";");
            for (int j = 1; j < s.length; ++j)
            {
                have = true;
                lines.add(i + j, s[j]);
            }
            if (have)
            { continue; }
            s = ln.split("\\§");
            lines.set(i, s[0]);
            for (int j = 1; j < s.length; ++j)
            {
                have = true;
                lines.add(i + j, s[j]);
                toAdd.add(i + j);
            }
        }
        for (Integer i : toAdd)
        {
            lines.set(i, "§" + lines.get(i));
        }
        StringBuilder sb = new StringBuilder();
        for (String s : lines)
        {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
    boolean isNormalEnd(String ln)
    {
        return ln.endsWith("\\.") || ln.endsWith("\\?") || ln.endsWith("\\!") || ln.endsWith("\\:");
    }
    private String replaceBigSpaces(String line)
    {
        int length = line.length();
        line = line.replaceAll("  ", " ");
        while (line.length() < length)
        {
            length = line.length();
            line = line.replaceAll("  ", " ");
        }
        return line;
    }
}