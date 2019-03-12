package ru.SemperAnte.TextParser;

import java.io.UnsupportedEncodingException;
import java.util.*;

public final class Parser
{


   private static final Map<Character, Character> EnglishRussian = new HashMap<>();
   private static final Map<Character, Character> RussianEnglish = new HashMap<>();
   private static final String EndsLineRegex = "[.!?;:]$";
   private static final String nonEndLine = "[\\[,\"\\]\\(\\)\\-\\–]";

   static
   {

      final char[] english = "qwertyuiop[]asdfghjkl;\'zxcvbnm,./".toCharArray();
      final char[] russian = "йцукенгшщзхъфывапролджэячсмитьбю.".toCharArray();
      final char[] specialEnsligh = ":\"|<>?@#$%^&{}".toCharArray();
      final char[] specialRussian = "ЖЭ/БЮ,\"№;%:?ХЪ".toCharArray();
      for (int i = 0; i < specialEnsligh.length; ++i)
      {
         EnglishRussian.put(specialEnsligh[i], specialRussian[i]);
         RussianEnglish.put(specialRussian[i], specialEnsligh[i]);
      }

      for (int i = 0; i < english.length; ++i)
      {
         EnglishRussian.put(english[i], russian[i]);
         RussianEnglish.put(russian[i], english[i]);

      }
   }

   static String parseFromString(String str, String from, String to) throws UnsupportedEncodingException
   {
      String s = new String(str.getBytes(from), to);
      ArrayList strings = new ArrayList(Arrays.asList(s.split("\n")));
      return parseText(strings);
   }

   static String parseTextNew(List<String> lines)
   {

      for (int i = 0; i < lines.size() - 1; ++i)
      {
         String ln = lines.get(i);
         if (ln.matches("\\s+$"))
         {
            ln = ln.replaceAll("\\s+$", " ") + lines.get(i + 1);
            lines.set(i, ln);
            lines.remove(i + 1);
         }
         if (ln.matches("^\\s+") && !isEnumeric(ln) && i > 0)
         {
            ln = ln.replaceAll("^\\s+", "");
            lines.set(i - 1, lines.get(i - 1) + ln);
            lines.remove(i);
            --i;
            continue;
         }

         if (ln.endsWith(":") && !isEnumeric(lines.get(i + 1)))
         {
            ln = ln + lines.get(i + 1);
            lines.set(i, ln);
            lines.remove(i + 1);
         }
         if (!ln.matches(EndsLineRegex))
         {
            ln = ln + lines.get(i + 1);
            lines.set(i, ln);
            lines.remove(i + 1);
         }
      }
      return toString(lines);
   }

   private static String toString(List<String> str)
   {
      if (str.size() == 0)
      {
         return "";
      }
      StringBuilder sb = new StringBuilder(str.get(0));
      for (int i = 1; i < str.size(); ++i)
      {
         sb.append("\n").append(str.get(i));
      }
      return sb.toString();
   }

   private static boolean isEnumeric(String ln)
   {
      return ln.matches("^(?i)((([\\da-bа-яMDCLXVI]+)\\s*[-.)–])|(\\s*[•o\uF0A7]))");
   }


   static String parseText(List<String> lines)
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
         {
            continue;
         }
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

   static String replaceBigSpaces(String line)
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

   static String toOneLineAll(String text)
   {
      return replaceBigSpaces(text.replaceAll("\n", " "));
   }

   static String removeWiki(String text)
   {
      return replaceBigSpaces(text.replaceAll("\\[\\d*\\]", ""));
   }

   static String removeBraces(String text)
   {
      return replaceBigSpaces(text.replaceAll("\\{.*\\}", ""));
   }

   static String removeHTML(String text)
   {
      return text.replaceAll("<br>", "\n").replaceAll("<[^>]*>", "").replaceAll("&.*;", "").replaceAll("\r\r", "\n").trim();
   }

   static String fromEnglish(String text)
   {
      char[] arr = text.toCharArray();
      for (int i = 0; i < arr.length; ++i)
      {
         boolean upperCase = false;
         if (Character.isUpperCase(arr[i]))
         {
            upperCase = true;
         }

         if (EnglishRussian.containsKey(arr[i]))
         {
            arr[i] = EnglishRussian.get(arr[i]);
            continue;
         }
         else if (EnglishRussian.containsKey(Character.toLowerCase(arr[i])))
         {
            arr[i] = EnglishRussian.get(Character.toLowerCase(arr[i]));
            if (upperCase)
            {
               arr[i] = Character.toUpperCase(arr[i]);
            }
         }

      }
      return new String(arr);
   }

   static String fromRussian(String text)
   {
      char[] arr = text.toCharArray();
      for (int i = 0; i < arr.length; ++i)
      {
         boolean upperCase = false;
         if (Character.isUpperCase(arr[i]))
         {
            upperCase = true;
         }

         if (RussianEnglish.containsKey(arr[i]))
         {
            arr[i] = RussianEnglish.get(arr[i]);
            continue;
         }
         else if (RussianEnglish.containsKey(Character.toLowerCase(arr[i])))
         {
            arr[i] = RussianEnglish.get(Character.toLowerCase(arr[i]));
            if (upperCase)
            {
               arr[i] = Character.toUpperCase(arr[i]);
            }
         }
      }
      return new String(arr);
   }

}
