/*
 *
 * MIT License - Copyright (c) 2025 TheFaRHaNiR
 * Permission is granted to use, copy, modify, and distribute this software,
 * provided the copyright notice and this permission notice are included.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 *
 * @Author: TheFaRHaNiR
 * @Link: https://github.com/TheFaRHaNiR
 *
 */

package TheFaRHaNiR.PersianChatFixer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java port of PersianTextEngine.php.
 * Glyph order per letter: [isolated, initial, medial, final] (same as PHP source).
 */
public final class PersianTextEngine {

    private static final Map<Character, char[]> GLYPHS = new HashMap<>();
    private static final Map<Character, Boolean> NON_CONNECTORS = new HashMap<>();

    static {
        GLYPHS.put('آ', chars("ﺁﺁﺂﺂ"));
        GLYPHS.put('ا', chars("ﺍﺍﺎﺎ"));
        GLYPHS.put('ب', chars("ﺏﺑﺒﺐ"));
        GLYPHS.put('پ', chars("ﭖﭘﭙﭗ"));
        GLYPHS.put('ت', chars("ﺕﺗﺘﺖ"));
        GLYPHS.put('ث', chars("ﺙﺛﺜﺚ"));
        GLYPHS.put('ج', chars("ﺝﺟﺠﺞ"));
        GLYPHS.put('چ', chars("ﭺﭼﭽﭻ"));
        GLYPHS.put('ح', chars("ﺡﺣﺤﺢ"));
        GLYPHS.put('خ', chars("ﺥﺧﺨﺦ"));
        GLYPHS.put('د', chars("ﺩﺩﺪﺪ"));
        GLYPHS.put('ذ', chars("ﺫﺫﺬﺬ"));
        GLYPHS.put('ر', chars("ﺭﺭﺮﺮ"));
        GLYPHS.put('ز', chars("ﺯﺯﺰﺰ"));
        GLYPHS.put('ژ', chars("ﮊﮊﮋﮋ"));
        GLYPHS.put('س', chars("ﺱﺳﺴﺲ"));
        GLYPHS.put('ش', chars("ﺵﺷﺸﺶ"));
        GLYPHS.put('ص', chars("ﺹﺻﺼﺺ"));
        GLYPHS.put('ض', chars("ﺽﺿﻀﺾ"));
        GLYPHS.put('ط', chars("ﻁﻃﻄﻂ"));
        GLYPHS.put('ظ', chars("ﻅﻇﻈﻆ"));
        GLYPHS.put('ع', chars("ﻉﻋﻌﻊ"));
        GLYPHS.put('غ', chars("ﻍﻏﻐﻎ"));
        GLYPHS.put('ف', chars("ﻑﻓﻔﻒ"));
        GLYPHS.put('ق', chars("ﻕﻗﻘﻖ"));
        GLYPHS.put('ک', chars("ﮎﮐﮑﮏ"));
        GLYPHS.put('گ', chars("ﮒﮔﮕﮓ"));
        GLYPHS.put('ل', chars("ﻝﻟﻠﻞ"));
        GLYPHS.put('م', chars("ﻡﻣﻤﻢ"));
        GLYPHS.put('ن', chars("ﻥﻧﻨﻦ"));
        GLYPHS.put('و', chars("ﻭﻭﻮﻮ"));
        GLYPHS.put('ه', chars("ﻩﻫﻬﻪ"));
        GLYPHS.put('ی', chars("ﯼﯾﯿﯽ"));
        GLYPHS.put('ئ', chars("ﺉﺋﺌﺊ"));
        GLYPHS.put('ء', chars("ﺀﺀﺀﺀ"));

        NON_CONNECTORS.put('ا', true);
        NON_CONNECTORS.put('آ', true);
        NON_CONNECTORS.put('د', true);
        NON_CONNECTORS.put('ذ', true);
        NON_CONNECTORS.put('ر', true);
        NON_CONNECTORS.put('ز', true);
        NON_CONNECTORS.put('ژ', true);
        NON_CONNECTORS.put('و', true);
    }

    private static final Pattern HAS_ARABIC = Pattern.compile("\\p{IsArabic}");
    private static final Pattern ONLY_NUMBERS = Pattern.compile("^\\p{N}+$");
    private static final Pattern ONLY_SYMBOLS = Pattern.compile("^[><\\[\\]]+$");
    private static final Pattern LEADING_COLOR = Pattern.compile("^(?:§.)+");
    private static final Pattern LEADING_SYMBOL = Pattern.compile("^([><\\[\\]]+)(\\s*)", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern TOKENIZE = Pattern.compile("(?:§.)*(?:\\([^)]*\\)|\\[[^\\]]*]|\\{[^}]*}|<[^>]*>|\\s+|[^\\s(){}\\[\\]<>]+)", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern PREFIX_SPLIT = Pattern.compile("^(?:§.)+");
    private static final Pattern WHITESPACE_ONLY = Pattern.compile("^\\s+$", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern BRACKETED = Pattern.compile("^[(\\[{<].*[)\\]}>]$", Pattern.DOTALL);
    private static final Pattern BRACKET_START = Pattern.compile("^(?:§.)*[(\\[{<]");
    private static final Pattern HAS_ASCII_ALNUM = Pattern.compile("[A-Za-z0-9]");
    private static final Pattern TRAILING_PUNCT = Pattern.compile("^(.*?)([\\p{P}\\p{S}]+)$");

    private PersianTextEngine() {
    }

    public static String process(String text) {
        return reversePersianText(correctPersianText(text));
    }

    public static String reversePersianText(String text) {
        Matcher colorMatcher = LEADING_COLOR.matcher(text);
        String leadingColor = "";
        if (colorMatcher.find()) {
            leadingColor = colorMatcher.group();
            text = text.substring(leadingColor.length());
        }

        Matcher symbolMatcher = LEADING_SYMBOL.matcher(text);
        String leadingSymbol = "";
        if (symbolMatcher.find()) {
            leadingSymbol = symbolMatcher.group(1) + (symbolMatcher.groupCount() > 1 ? symbolMatcher.group(2) : "");
            text = text.substring(leadingSymbol.length());
        }

        if (!HAS_ARABIC.matcher(text).find()) {
            return leadingColor + leadingSymbol + text;
        }

        List<String> tokens = new ArrayList<>();
        Matcher tokenizer = TOKENIZE.matcher(text);
        while (tokenizer.find()) {
            tokens.add(tokenizer.group());
        }

        List<String> merged = new ArrayList<>();
        int n = tokens.size();
        int i = 0;
        while (i < n) {
            String t = tokens.get(i);

            if (WHITESPACE_ONLY.matcher(t).matches()) {
                merged.add(t);
                i++;
                continue;
            }

            if (BRACKETED.matcher(t).matches()) {
                merged.add(t);
                i++;
                continue;
            }

            String core = splitPrefix(t)[1];
            if (HAS_ASCII_ALNUM.matcher(core).find()) {
                StringBuilder buf = new StringBuilder(t);
                int j = i + 1;
                while (j + 1 < n) {
                    if (!WHITESPACE_ONLY.matcher(tokens.get(j)).matches()) {
                        break;
                    }
                    String nextCore = splitPrefix(tokens.get(j + 1))[1];
                    if (!HAS_ASCII_ALNUM.matcher(nextCore).find()) {
                        break;
                    }
                    buf.append(tokens.get(j)).append(tokens.get(j + 1));
                    j += 2;
                }
                merged.add(buf.toString());
                i = j;
                continue;
            }

            merged.add(t);
            i++;
        }

        String lastPrefix = leadingColor;
        List<Token> tokenObjs = new ArrayList<>();
        for (String t : merged) {
            if (WHITESPACE_ONLY.matcher(t).matches()) {
                tokenObjs.add(new Token(t, "", t, ""));
                continue;
            }

            String[] parts = splitPrefix(t);
            String pref = parts[0];
            String core = parts[1];

            String applied;
            if (!pref.isEmpty()) {
                lastPrefix = pref;
                applied = pref;
            } else {
                applied = lastPrefix;
            }

            tokenObjs.add(new Token(t, pref, core, applied));
        }

        java.util.Collections.reverse(tokenObjs);

        StringBuilder out = new StringBuilder();
        for (Token tokObj : tokenObjs) {
            String raw = tokObj.raw;
            String core = tokObj.core;
            String colorForToken = tokObj.applied;

            if (WHITESPACE_ONLY.matcher(raw).matches()) {
                out.append(raw);
                continue;
            }

            if (BRACKET_START.matcher(raw).find()) {
                String inner;
                if (core.startsWith("(") && core.endsWith(")") && core.length() >= 2) {
                    inner = reversePersianText(core.substring(1, core.length() - 1));
                    out.append(colorForToken).append('(').append(inner).append(')');
                    continue;
                }
                if (core.startsWith("[") && core.endsWith("]") && core.length() >= 2) {
                    inner = reversePersianText(core.substring(1, core.length() - 1));
                    out.append(colorForToken).append('[').append(inner).append(']');
                    continue;
                }
                if (core.startsWith("{") && core.endsWith("}") && core.length() >= 2) {
                    inner = reversePersianText(core.substring(1, core.length() - 1));
                    out.append(colorForToken).append('{').append(inner).append('}');
                    continue;
                }
                if (core.startsWith("<") && core.endsWith(">") && core.length() >= 2) {
                    inner = reversePersianText(core.substring(1, core.length() - 1));
                    out.append(colorForToken).append('<').append(inner).append('>');
                    continue;
                }
            }

            if (ONLY_SYMBOLS.matcher(core).matches()) {
                out.append(colorForToken).append(core);
                continue;
            }

            if (!HAS_ARABIC.matcher(core).find() && HAS_ASCII_ALNUM.matcher(core).find()) {
                Matcher pm = TRAILING_PUNCT.matcher(core);
                if (pm.matches()) {
                    String word = pm.group(1);
                    String pun = pm.group(2);
                    core = pun + word;
                }
                out.append(colorForToken).append(core);
                continue;
            }

            if (HAS_ARABIC.matcher(core).find() && !ONLY_NUMBERS.matcher(core).matches()) {
                out.append(colorForToken).append(new StringBuilder(core).reverse());
                continue;
            }

            out.append(colorForToken).append(core);
        }

        return leadingSymbol + out;
    }

    public static String correctPersianText(String text) {
        int[] cps = text.codePoints().toArray();
        int count = cps.length;
        StringBuilder result = new StringBuilder(count);

        boolean skipNext = false;
        for (int i = 0; i < count; i++) {
            if (skipNext) {
                skipNext = false;
                continue;
            }

            char curr = toChar(cps, i);
            char prev = i > 0 ? toChar(cps, i - 1) : '\0';
            char next = i < count - 1 ? toChar(cps, i + 1) : '\0';

            if (curr == 'ل' && next == 'ا') {
                boolean prevGlyph = i > 0 && GLYPHS.containsKey(prev);
                boolean connectsBefore = prevGlyph && !NON_CONNECTORS.containsKey(prev);

                result.appendCodePoint(connectsBefore ? 'ﻼ' : 'ﻻ');
                skipNext = true;
                continue;
            }

            char[] forms = GLYPHS.get(curr);
            if (forms == null) {
                result.appendCodePoint(cps[i]);
                continue;
            }

            boolean prevGlyph = i > 0 && GLYPHS.containsKey(prev);
            boolean nextGlyph = i < count - 1 && GLYPHS.containsKey(next);

            boolean connectsBefore = prevGlyph && !NON_CONNECTORS.containsKey(prev);
            boolean connectsAfter = !NON_CONNECTORS.containsKey(curr) && nextGlyph;

            int form;
            if (connectsBefore) {
                form = connectsAfter ? 2 : 3;
            } else if (connectsAfter) {
                form = 1;
            } else {
                form = 0;
            }

            result.append(forms[form]);
        }

        return result.toString();
    }

    private static String[] splitPrefix(String token) {
        Matcher pm = PREFIX_SPLIT.matcher(token);
        if (pm.find()) {
            String pref = pm.group();
            String core = token.substring(pref.length());
            return new String[]{pref, core};
        }
        return new String[]{"", token};
    }

    private static char toChar(int[] cps, int index) {
        return (char) cps[index];
    }

    private static char[] chars(String s) {
        return s.toCharArray();
    }

    private record Token(String raw, String prefix, String core, String applied) {
    }
}
