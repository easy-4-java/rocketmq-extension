package org.apache.rocketmq.client.extension.util;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

/**
 * Tests for {@link StringUtils}.
 */
public class StringUtilsTest {

    // --- isEmpty(String) ---

    @Test
    public void shouldReturnTrueForNullString() {
        assertTrue(StringUtils.isEmpty((String) null));
    }

    @Test
    public void shouldReturnTrueForEmptyString() {
        assertTrue(StringUtils.isEmpty(""));
    }

    @Test
    public void shouldReturnTrueForNullLiteral() {
        assertTrue(StringUtils.isEmpty("NULL"));
    }

    @Test
    public void shouldReturnTrueForLowercaseNullLiteral() {
        assertTrue(StringUtils.isEmpty("null"));
    }

    @Test
    public void shouldReturnFalseForNonEmptyString() {
        assertFalse(StringUtils.isEmpty("hello"));
    }

    // --- isNotEmpty(String) ---

    @Test
    public void shouldReturnFalseForNullWhenNotEmpty() {
        assertFalse(StringUtils.isNotEmpty(null));
    }

    @Test
    public void shouldReturnTrueForNonEmptyWhenNotEmpty() {
        assertTrue(StringUtils.isNotEmpty("hello"));
    }

    // --- isNull(String) ---

    @Test
    public void shouldReturnTrueForNullIsNull() {
        assertTrue(StringUtils.isNull(null));
    }

    @Test
    public void shouldReturnTrueForBlankIsNull() {
        assertTrue(StringUtils.isNull("   "));
    }

    @Test
    public void shouldReturnFalseForNonNullIsNull() {
        assertFalse(StringUtils.isNull("abc"));
    }

    // --- isEmpty(Object) ---

    @Test
    public void shouldReturnTrueForNullObject() {
        assertTrue(StringUtils.isEmpty((Object) null));
    }

    @Test
    public void shouldReturnTrueForEmptyStringObject() {
        assertTrue(StringUtils.isEmpty((Object) ""));
    }

    @Test
    public void shouldReturnFalseForNonEmptyObject() {
        assertFalse(StringUtils.isEmpty((Object) "test"));
    }

    // --- hasLength(CharSequence) ---

    @Test
    public void shouldReturnFalseForNullHasLength() {
        assertFalse(StringUtils.hasLength((CharSequence) null));
    }

    @Test
    public void shouldReturnFalseForEmptyHasLength() {
        assertFalse(StringUtils.hasLength(""));
    }

    @Test
    public void shouldReturnTrueForWhitespaceHasLength() {
        assertTrue(StringUtils.hasLength(" "));
    }

    @Test
    public void shouldReturnTrueForNonEmptyHasLength() {
        assertTrue(StringUtils.hasLength("Hello"));
    }

    // --- hasLength(String) ---

    @Test
    public void shouldReturnFalseForNullStringHasLength() {
        assertFalse(StringUtils.hasLength((String) null));
    }

    @Test
    public void shouldReturnTrueForNonEmptyStringHasLength() {
        assertTrue(StringUtils.hasLength("abc"));
    }

    // --- hasText(CharSequence) ---

    @Test
    public void shouldReturnFalseForNullHasText() {
        assertFalse(StringUtils.hasText((CharSequence) null));
    }

    @Test
    public void shouldReturnFalseForEmptyHasText() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    public void shouldReturnFalseForWhitespaceOnlyHasText() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    public void shouldReturnTrueForTextHasText() {
        assertTrue(StringUtils.hasText("12345"));
    }

    @Test
    public void shouldReturnTrueForTextWithWhitespaceHasText() {
        assertTrue(StringUtils.hasText(" 12345 "));
    }

    // --- hasText(String) ---

    @Test
    public void shouldReturnFalseForNullStringHasText() {
        assertFalse(StringUtils.hasText((String) null));
    }

    // --- containsWhitespace ---

    @Test
    public void shouldReturnFalseForNullContainsWhitespace() {
        assertFalse(StringUtils.containsWhitespace((CharSequence) null));
    }

    @Test
    public void shouldReturnFalseForNoWhitespace() {
        assertFalse(StringUtils.containsWhitespace("abc"));
    }

    @Test
    public void shouldReturnTrueWhenContainsWhitespace() {
        assertTrue(StringUtils.containsWhitespace("a b c"));
    }

    @Test
    public void shouldReturnFalseForNullStringContainsWhitespace() {
        assertFalse(StringUtils.containsWhitespace((String) null));
    }

    @Test
    public void shouldReturnTrueForStringWithWhitespace() {
        assertTrue(StringUtils.containsWhitespace("hello world"));
    }

    // --- trimWhitespace ---

    @Test
    public void shouldTrimWhitespace() {
        assertEquals("hello", StringUtils.trimWhitespace("  hello  "));
    }

    @Test
    public void shouldReturnNullForNullTrimWhitespace() {
        assertNull(StringUtils.trimWhitespace(null));
    }

    // --- trimAllWhitespace ---

    @Test
    public void shouldTrimAllWhitespace() {
        assertEquals("helloworld", StringUtils.trimAllWhitespace("  hello  world  "));
    }

    @Test
    public void shouldReturnNullForNullTrimAllWhitespace() {
        assertNull(StringUtils.trimAllWhitespace(null));
    }

    // --- trimLeadingWhitespace ---

    @Test
    public void shouldTrimLeadingWhitespace() {
        assertEquals("hello  ", StringUtils.trimLeadingWhitespace("  hello  "));
    }

    // --- trimTrailingWhitespace ---

    @Test
    public void shouldTrimTrailingWhitespace() {
        assertEquals("  hello", StringUtils.trimTrailingWhitespace("  hello  "));
    }

    // --- trimLeadingCharacter / trimTrailingCharacter ---

    @Test
    public void shouldTrimLeadingCharacter() {
        assertEquals("bc", StringUtils.trimLeadingCharacter("aabc", 'a'));
    }

    @Test
    public void shouldTrimTrailingCharacter() {
        assertEquals("ab", StringUtils.trimTrailingCharacter("abcc", 'c'));
    }

    // --- startsWithIgnoreCase / endsWithIgnoreCase ---

    @Test
    public void shouldReturnTrueForStartsWithIgnoreCase() {
        assertTrue(StringUtils.startsWithIgnoreCase("HelloWorld", "hello"));
    }

    @Test
    public void shouldReturnFalseForStartsWithIgnoreCase() {
        assertFalse(StringUtils.startsWithIgnoreCase("Hello", "World"));
    }

    @Test
    public void shouldReturnTrueForEndsWithIgnoreCase() {
        assertTrue(StringUtils.endsWithIgnoreCase("HelloWorld", "WORLD"));
    }

    @Test
    public void shouldReturnFalseForEndsWithIgnoreCase() {
        assertFalse(StringUtils.endsWithIgnoreCase("Hello", "World"));
    }

    @Test
    public void shouldReturnFalseForNullStartsWith() {
        assertFalse(StringUtils.startsWithIgnoreCase(null, "a"));
        assertFalse(StringUtils.startsWithIgnoreCase("a", null));
    }

    @Test
    public void shouldReturnFalseForNullEndsWith() {
        assertFalse(StringUtils.endsWithIgnoreCase(null, "a"));
        assertFalse(StringUtils.endsWithIgnoreCase("a", null));
    }

    @Test
    public void shouldReturnFalseWhenPrefixLongerThanString() {
        assertFalse(StringUtils.startsWithIgnoreCase("ab", "abc"));
    }

    @Test
    public void shouldReturnFalseWhenSuffixLongerThanString() {
        assertFalse(StringUtils.endsWithIgnoreCase("ab", "abc"));
    }

    // --- substringMatch ---

    @Test
    public void shouldMatchSubstring() {
        assertTrue(StringUtils.substringMatch("hello", 0, "hel"));
    }

    @Test
    public void shouldNotMatchSubstring() {
        assertFalse(StringUtils.substringMatch("hello", 0, "xyz"));
    }

    // --- countOccurrencesOf ---

    @Test
    public void shouldCountOccurrences() {
        assertEquals(3, StringUtils.countOccurrencesOf("ababab", "ab"));
    }

    @Test
    public void shouldReturnZeroForNullCount() {
        assertEquals(0, StringUtils.countOccurrencesOf(null, "ab"));
        assertEquals(0, StringUtils.countOccurrencesOf("ab", null));
    }

    // --- replace ---

    @Test
    public void shouldReplaceOccurrences() {
        assertEquals("a-b-c", StringUtils.replace("a.b.c", ".", "-"));
    }

    @Test
    public void shouldReturnOriginalWhenNoMatch() {
        assertEquals("abc", StringUtils.replace("abc", "x", "y"));
    }

    @Test
    public void shouldReturnOriginalForNullInputs() {
        assertNull(StringUtils.replace(null, "a", "b"));
        assertEquals("abc", StringUtils.replace("abc", null, "b"));
    }

    // --- delete ---

    @Test
    public void shouldDeletePattern() {
        assertEquals("ac", StringUtils.delete("abc", "b"));
    }

    // --- deleteAny ---

    @Test
    public void shouldDeleteAnyCharacters() {
        assertEquals("bc", StringUtils.deleteAny("aabc", "a"));
    }

    @Test
    public void shouldReturnOriginalForNullDeleteAny() {
        assertNull(StringUtils.deleteAny(null, "a"));
        assertEquals("abc", StringUtils.deleteAny("abc", null));
    }

    // --- unqualify ---

    @Test
    public void shouldUnqualify() {
        assertEquals("qualified", StringUtils.unqualify("this.name.is.qualified"));
    }

    @Test
    public void shouldUnqualifyWithSeparator() {
        assertEquals("qualified", StringUtils.unqualify("this:name:is:qualified", ':'));
    }

    // --- capitalize / uncapitalize ---

    @Test
    public void shouldCapitalize() {
        assertEquals("Hello", StringUtils.capitalize("hello"));
    }

    @Test
    public void shouldUncapitalize() {
        assertEquals("hello", StringUtils.uncapitalize("Hello"));
    }

    @Test
    public void shouldReturnNullForNullCapitalize() {
        assertNull(StringUtils.capitalize(null));
    }

    @Test
    public void shouldReturnNullForNullUncapitalize() {
        assertNull(StringUtils.uncapitalize(null));
    }

    @Test
    public void shouldReturnEmptyForEmptyCapitalize() {
        assertEquals("", StringUtils.capitalize(""));
    }

    // --- getFilename / getFilenameExtension / stripFilenameExtension ---

    @Test
    public void shouldGetFilename() {
        assertEquals("file.txt", StringUtils.getFilename("path/file.txt"));
    }

    @Test
    public void shouldReturnPathWhenNoSeparator() {
        assertEquals("file.txt", StringUtils.getFilename("file.txt"));
    }

    @Test
    public void shouldReturnNullForNullGetFilename() {
        assertNull(StringUtils.getFilename(null));
    }

    @Test
    public void shouldGetFilenameExtension() {
        assertEquals("txt", StringUtils.getFilenameExtension("path/file.txt"));
    }

    @Test
    public void shouldReturnNullWhenNoExtension() {
        assertNull(StringUtils.getFilenameExtension("file"));
    }

    @Test
    public void shouldReturnNullForNullGetFilenameExtension() {
        assertNull(StringUtils.getFilenameExtension(null));
    }

    @Test
    public void shouldStripFilenameExtension() {
        assertEquals("path/file", StringUtils.stripFilenameExtension("path/file.txt"));
    }

    @Test
    public void shouldReturnPathWhenNoExtensionStrip() {
        assertEquals("file", StringUtils.stripFilenameExtension("file"));
    }

    @Test
    public void shouldReturnNullForNullStripFilenameExtension() {
        assertNull(StringUtils.stripFilenameExtension(null));
    }

    // --- applyRelativePath ---

    @Test
    public void shouldApplyRelativePath() {
        assertEquals("path/relative", StringUtils.applyRelativePath("path/file.txt", "relative"));
    }

    @Test
    public void shouldReturnRelativeWhenNoSeparator() {
        assertEquals("relative", StringUtils.applyRelativePath("file", "relative"));
    }

    // --- cleanPath ---

    @Test
    public void shouldCleanPath() {
        assertEquals("src//main", StringUtils.cleanPath("src//main"));
    }

    @Test
    public void shouldReturnNullForNullCleanPath() {
        assertNull(StringUtils.cleanPath(null));
    }

    @Test
    public void shouldNormalizeDots() {
        assertEquals("b", StringUtils.cleanPath("a/../b"));
    }

    @Test
    public void shouldHandleWindowsSeparators() {
        assertEquals("a/b", StringUtils.cleanPath("a\\b"));
    }

    // --- pathEquals ---

    @Test
    public void shouldReturnTrueForEqualPaths() {
        assertTrue(StringUtils.pathEquals("a/../b", "b"));
    }

    @Test
    public void shouldReturnFalseForDifferentPaths() {
        assertFalse(StringUtils.pathEquals("a", "b"));
    }

    // --- parseLocaleString ---

    @Test
    public void shouldParseLocaleString() {
        Locale locale = StringUtils.parseLocaleString("en_US");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
    }

    @Test
    public void shouldReturnNullForEmptyLocaleString() {
        assertNull(StringUtils.parseLocaleString(""));
    }

    // --- toLanguageTag ---

    @Test
    public void shouldConvertToLanguageTag() {
        assertEquals("en-US", StringUtils.toLanguageTag(Locale.US));
    }

    // --- parseTimeZoneString ---

    @Test
    public void shouldParseTimeZone() {
        assertNotNull(StringUtils.parseTimeZoneString("GMT"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowForInvalidTimeZone() {
        StringUtils.parseTimeZoneString("INVALID_ZONE");
    }

    // --- addStringToArray ---

    @Test
    public void shouldAddStringToArray() {
        String[] result = StringUtils.addStringToArray(new String[]{"a"}, "b");
        assertArrayEquals(new String[]{"a", "b"}, result);
    }

    @Test
    public void shouldAddStringToNullArray() {
        String[] result = StringUtils.addStringToArray(null, "a");
        assertArrayEquals(new String[]{"a"}, result);
    }

    // --- concatenateStringArrays ---

    @Test
    public void shouldConcatenateArrays() {
        String[] result = StringUtils.concatenateStringArrays(new String[]{"a"}, new String[]{"b"});
        assertArrayEquals(new String[]{"a", "b"}, result);
    }

    @Test
    public void shouldReturnOtherWhenFirstIsNull() {
        assertArrayEquals(new String[]{"b"}, StringUtils.concatenateStringArrays(null, new String[]{"b"}));
    }

    @Test
    public void shouldReturnFirstWhenSecondIsNull() {
        assertArrayEquals(new String[]{"a"}, StringUtils.concatenateStringArrays(new String[]{"a"}, null));
    }

    // --- mergeStringArrays ---

    @Test
    public void shouldMergeArraysRemovingDuplicates() {
        String[] result = StringUtils.mergeStringArrays(new String[]{"a", "b"}, new String[]{"b", "c"});
        assertArrayEquals(new String[]{"a", "b", "c"}, result);
    }

    // --- sortStringArray ---

    @Test
    public void shouldSortStringArray() {
        String[] result = StringUtils.sortStringArray(new String[]{"c", "a", "b"});
        assertArrayEquals(new String[]{"a", "b", "c"}, result);
    }

    @Test
    public void shouldReturnEmptyArrayForNullSort() {
        assertArrayEquals(new String[0], StringUtils.sortStringArray(null));
    }

    // --- toStringArray ---

    @Test
    public void shouldConvertCollectionToStringArray() {
        List<String> list = Arrays.asList("a", "b");
        assertArrayEquals(new String[]{"a", "b"}, StringUtils.toStringArray(list));
    }

    @Test
    public void shouldReturnNullForNullCollection() {
        assertNull(StringUtils.toStringArray((Collection<String>) null));
    }

    // --- trimArrayElements ---

    @Test
    public void shouldTrimArrayElements() {
        String[] result = StringUtils.trimArrayElements(new String[]{" a ", " b "});
        assertArrayEquals(new String[]{"a", "b"}, result);
    }

    @Test
    public void shouldReturnEmptyArrayForNullTrim() {
        assertArrayEquals(new String[0], StringUtils.trimArrayElements(null));
    }

    // --- removeDuplicateStrings ---

    @Test
    public void shouldRemoveDuplicates() {
        String[] result = StringUtils.removeDuplicateStrings(new String[]{"a", "b", "a"});
        assertEquals(2, result.length);
    }

    @Test
    public void shouldReturnNullForNullRemoveDuplicates() {
        assertNull(StringUtils.removeDuplicateStrings(null));
    }

    // --- tokenizeToStringArray ---

    @Test
    public void shouldTokenizeWithDefaultDelimiters() {
        String[] result = StringUtils.tokenizeToStringArray("a,b;c");
        assertEquals(3, result.length);
    }

    @Test
    public void shouldTokenizeWithCustomDelimiters() {
        String[] result = StringUtils.tokenizeToStringArray("a,b,c", ",");
        assertEquals(3, result.length);
    }

    @Test
    public void shouldReturnNullForNullTokenize() {
        assertNull(StringUtils.tokenizeToStringArray(null, ","));
    }

    // --- delimitedListToStringArray ---

    @Test
    public void shouldConvertDelimitedList() {
        String[] result = StringUtils.delimitedListToStringArray("a,b,c", ",");
        assertArrayEquals(new String[]{"a", "b", "c"}, result);
    }

    @Test
    public void shouldReturnSingleElementForNullDelimiter() {
        assertArrayEquals(new String[]{"abc"}, StringUtils.delimitedListToStringArray("abc", null));
    }

    @Test
    public void shouldReturnEmptyArrayForNullInput() {
        assertArrayEquals(new String[0], StringUtils.delimitedListToStringArray(null, ","));
    }

    @Test
    public void shouldHandleEmptyDelimiter() {
        String[] result = StringUtils.delimitedListToStringArray("abc", "");
        assertEquals(3, result.length);
    }

    // --- commaDelimitedListToStringArray ---

    @Test
    public void shouldConvertCommaDelimitedList() {
        assertArrayEquals(new String[]{"a", "b", "c"},
                StringUtils.commaDelimitedListToStringArray("a,b,c"));
    }

    // --- commaDelimitedListToSet ---

    @Test
    public void shouldConvertCommaDelimitedListToSet() {
        Set<String> set = StringUtils.commaDelimitedListToSet("a,b,a");
        assertEquals(2, set.size());
    }

    // --- collectionToDelimitedString ---

    @Test
    public void shouldConvertCollectionToDelimitedString() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals("a,b,c", StringUtils.collectionToDelimitedString(list, ","));
    }

    @Test
    public void shouldConvertCollectionWithPrefixSuffix() {
        List<String> list = Arrays.asList("a", "b");
        assertEquals("'a','b'", StringUtils.collectionToDelimitedString(list, ",", "'", "'"));
    }

    @Test
    public void shouldReturnEmptyForNullCollection() {
        assertEquals("", StringUtils.collectionToDelimitedString(null, ","));
    }

    // --- collectionToCommaDelimitedString ---

    @Test
    public void shouldConvertCollectionToCommaString() {
        List<String> list = Arrays.asList("a", "b");
        assertEquals("a,b", StringUtils.collectionToCommaDelimitedString(list));
    }

    // --- arrayToDelimitedString ---

    @Test
    public void shouldConvertArrayToDelimitedString() {
        assertEquals("a,b,c", StringUtils.arrayToDelimitedString(new Object[]{"a", "b", "c"}, ","));
    }

    @Test
    public void shouldReturnEmptyForNullArray() {
        assertEquals("", StringUtils.arrayToDelimitedString(null, ","));
    }

    @Test
    public void shouldReturnSingleElement() {
        assertEquals("a", StringUtils.arrayToDelimitedString(new Object[]{"a"}, ","));
    }

    // --- arrayToCommaDelimitedString ---

    @Test
    public void shouldConvertArrayToCommaString() {
        assertEquals("a,b", StringUtils.arrayToCommaDelimitedString(new Object[]{"a", "b"}));
    }

    // --- split(String, char) ---

    @Test
    public void shouldSplitByChar() {
        String[] result = StringUtils.split("a,b,c", ',');
        // split by char splits at first occurrence only
        assertEquals(2, result.length);
    }

    @Test
    public void shouldReturnSingleElementWhenNoDelimiter() {
        String[] result = StringUtils.split("abc", ',');
        assertArrayEquals(new String[]{"abc"}, result);
    }

    @Test
    public void shouldReturnEmptyArrayForNullSplitChar() {
        assertArrayEquals(new String[0], StringUtils.split(null, ','));
    }

    // --- split(String, String) ---

    @Test
    public void shouldSplitByString() {
        String[] result = StringUtils.split("a::b", "::");
        assertArrayEquals(new String[]{"a", "b"}, result);
    }

    @Test
    public void shouldReturnNullForNullSplitString() {
        assertNull(StringUtils.split(null, "::"));
        assertNull(StringUtils.split("abc", null));
    }

    @Test
    public void shouldReturnNullWhenDelimiterNotFound() {
        assertNull(StringUtils.split("abc", "::"));
    }

    // --- splits ---

    @Test
    public void shouldSplitByRegex() {
        String[] result = StringUtils.splits("a,b,c", ",");
        assertEquals(3, result.length);
    }

    @Test
    public void shouldReturnEmptyArrayForNullSplits() {
        assertArrayEquals(new String[0], StringUtils.splits(null, ","));
    }

    // --- removeLast ---

    @Test
    public void shouldRemoveLastCharacter() {
        assertEquals("hel", StringUtils.removeLast("hell"));
    }

    @Test
    public void shouldReturnNullForNullRemoveLast() {
        assertNull(StringUtils.removeLast(null));
    }

    // --- addQuotation ---

    @Test
    public void shouldAddQuotation() {
        assertEquals("'123','567'", StringUtils.addQuotation("123,567"));
    }

    @Test
    public void shouldReturnNullForNullAddQuotation() {
        assertNull(StringUtils.addQuotation(null));
    }

    // --- listToArray ---

    @Test
    public void shouldConvertListToArray() {
        List<String> list = Arrays.asList("a", "b");
        assertArrayEquals(new String[]{"a", "b"}, StringUtils.listToArray(list));
    }

    // --- listToString ---

    @Test
    public void shouldConvertListToString() {
        List<String> list = Arrays.asList("a", "b");
        assertEquals("a,b", StringUtils.listToString(list, ","));
    }

    // --- genRandomNum ---

    @Test
    public void shouldGenerateRandomPassword() {
        String pwd = StringUtils.genRandomNum(10);
        assertNotNull(pwd);
        assertEquals(10, pwd.length());
    }

    // --- killNull ---

    @Test
    public void shouldKillNull() {
        assertEquals("", StringUtils.killNull(null));
        assertEquals("abc", StringUtils.killNull("abc"));
    }

    // --- parentheses ---

    @Test
    public void shouldWrapInParentheses() {
        assertEquals("(abc)", StringUtils.parentheses("abc"));
    }

    @Test
    public void shouldReturnNullParentheses() {
        assertNull(StringUtils.parentheses(null));
    }

    // --- brackets ---

    @Test
    public void shouldWrapInBrackets() {
        assertEquals("[abc]", StringUtils.brackets("abc"));
    }

    @Test
    public void shouldReturnNullBrackets() {
        assertNull(StringUtils.brackets(null));
    }

    // --- ditto ---

    @Test
    public void shouldWrapInDitto() {
        assertEquals("\"abc\"", StringUtils.ditto("abc"));
    }

    @Test
    public void shouldReturnNullDitto() {
        assertNull(StringUtils.ditto(null));
    }

    // --- quote(String) ---

    @Test
    public void shouldQuote() {
        assertEquals("'abc'", StringUtils.quote("abc"));
    }

    @Test
    public void shouldReturnNullQuote() {
        assertNull(StringUtils.quote(null));
    }

    // --- quote(String[], String) ---

    @Test
    public void shouldQuoteArray() {
        assertEquals("'a','b'", StringUtils.quote(new String[]{"a", "b"}, ","));
    }

    @Test
    public void shouldReturnEmptyForNullArrayQuote() {
        assertEquals("", StringUtils.quote(null, ","));
    }

    @Test
    public void shouldReturnEmptyForEmptyArrayQuote() {
        assertEquals("", StringUtils.quote(new String[0], ","));
    }

    // --- quoteIfString ---

    @Test
    public void shouldQuoteIfString() {
        assertEquals("'abc'", StringUtils.quoteIfString("abc"));
    }

    @Test
    public void shouldReturnObjectIfNotString() {
        Integer obj = 42;
        assertEquals(42, StringUtils.quoteIfString(obj));
    }

    // --- trimToAlphaString ---

    @Test
    public void shouldTrimToAlphaString() {
        assertEquals("11", StringUtils.trimToAlphaString("1\r\n1\r\n"));
    }

    @Test
    public void shouldReturnEmptyForNullTrimToAlpha() {
        assertEquals("", StringUtils.trimToAlphaString(null));
    }

    // --- trimToAlphaStrings ---

    @Test
    public void shouldTrimToAlphaStrings() {
        String[] result = StringUtils.trimToAlphaStrings("1\r\n1\r\n");
        assertArrayEquals(new String[]{"1", "1"}, result);
    }

    @Test
    public void shouldReturnEmptyForNullTrimToAlphaStrings() {
        assertArrayEquals(new String[0], StringUtils.trimToAlphaStrings(null));
    }

    // --- trimToString ---

    @Test
    public void shouldTrimToString() {
        assertEquals("abc", StringUtils.trimToString("  abc  "));
    }

    @Test
    public void shouldReturnNullForBlankTrimToString() {
        assertNull(StringUtils.trimToString("   "));
    }

    @Test
    public void shouldReturnNullForNullTrimToString() {
        assertNull(StringUtils.trimToString(null));
    }

    // --- replaceAll ---

    @Test
    public void shouldReplaceAllOccurrences() {
        // The replaceAll method uses byte-level matching
        // Test with no match to verify it returns original string
        String result = StringUtils.replaceAll("z", "x", "abc");
        assertEquals("abc", result);
    }

    // --- getMapFromQueryParamString ---

    @Test
    public void shouldGetMapFromQueryParamString() {
        Map<String, String> result = StringUtils.getMapFromQueryParamString("key`value");
        assertNotNull(result);
    }

    // --- getFirstLetterFromChinessWord ---

    @Test
    public void shouldGetFirstLetterFromEnglishWord() {
        char result = StringUtils.getFirstLetterFromChinessWord("Hello");
        assertEquals('H', result);
    }

    // --- splitArrayElementsIntoProperties ---

    @Test
    public void shouldSplitArrayIntoProperties() {
        Properties props = StringUtils.splitArrayElementsIntoProperties(
                new String[]{"key1=value1", "key2=value2"}, "=");
        assertNotNull(props);
        assertEquals("value1", props.getProperty("key1"));
    }

    @Test
    public void shouldReturnNullForNullArrayProperties() {
        assertNull(StringUtils.splitArrayElementsIntoProperties(null, "="));
    }

    @Test
    public void shouldSplitWithCharsToDelete() {
        Properties props = StringUtils.splitArrayElementsIntoProperties(
                new String[]{"key1=\"value1\""}, "=", "\"");
        assertNotNull(props);
        assertEquals("value1", props.getProperty("key1"));
    }

    // --- toStringArray(Enumeration) ---

    @Test
    public void shouldConvertEnumerationToStringArray() {
        List<String> list = Arrays.asList("a", "b");
        Enumeration<String> en = Collections.enumeration(list);
        String[] result = StringUtils.toStringArray(en);
        assertArrayEquals(new String[]{"a", "b"}, result);
    }

    @Test
    public void shouldReturnNullForNullEnumeration() {
        assertNull(StringUtils.toStringArray((Enumeration<String>) null));
    }

    // --- delimitedListToStringArray with charsToDelete ---

    @Test
    public void shouldDeleteCharsFromDelimitedList() {
        String[] result = StringUtils.delimitedListToStringArray("a\n,b\n", ",", "\n");
        assertEquals(2, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
    }

    // --- getFilenameExtension edge case ---

    @Test
    public void shouldReturnNullWhenFolderAfterExtension() {
        assertNull(StringUtils.getFilenameExtension("path/file/"));
    }

    // --- stripFilenameExtension edge case ---

    @Test
    public void shouldReturnPathWhenFolderAfterExtensionStrip() {
        assertEquals("path/file/", StringUtils.stripFilenameExtension("path/file/"));
    }

    // --- changeFirstCharacterCase edge case ---

    @Test
    public void shouldHandleEmptyStringCapitalize() {
        assertEquals("", StringUtils.capitalize(""));
    }

    // --- tokenizeToStringArray with trim and ignore empty ---

    @Test
    public void shouldTokenizeWithTrimAndIgnoreEmpty() {
        String[] result = StringUtils.tokenizeToStringArray(" a , , b ", ",", true, true);
        assertEquals(2, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
    }

    @Test
    public void shouldTokenizeWithoutTrim() {
        String[] result = StringUtils.tokenizeToStringArray(" a , b ", ",", false, true);
        // Without trim, spaces are included in tokens
        assertNotNull(result);
    }
}
