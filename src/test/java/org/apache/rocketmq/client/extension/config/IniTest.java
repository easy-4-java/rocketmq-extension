package org.apache.rocketmq.client.extension.config;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;

import org.junit.Test;

import org.apache.rocketmq.client.extension.exception.EventHandleException;

/**
 * Tests for {@link Ini} and {@link Ini.Section}.
 */
public class IniTest {

    @Test
    public void shouldCreateEmptyIni() {
        Ini ini = new Ini();
        assertTrue(ini.isEmpty());
        assertEquals(0, ini.size());
    }

    @Test
    public void shouldCreateIniFromDefaults() {
        Ini defaults = new Ini();
        defaults.addSection("test").put("key", "value");
        Ini copy = new Ini(defaults);
        assertNotNull(copy.getSection("test"));
        assertEquals("value", copy.getSectionProperty("test", "key"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenDefaultsIsNull() {
        new Ini(null);
    }

    @Test
    public void shouldLoadFromString() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[section1]\nkey1=value1\nkey2=value2\n");
        assertNotNull(ini.getSection("section1"));
        assertEquals("value1", ini.getSectionProperty("section1", "key1"));
        assertEquals("value2", ini.getSectionProperty("section1", "key2"));
    }

    @Test
    public void shouldLoadFromInputStream() throws IOException {
        String content = "[urls]\n/path/** = handler1\n";
        Ini ini = new Ini();
        ini.load(new ByteArrayInputStream(content.getBytes()));
        assertNotNull(ini.getSection("urls"));
    }

    @Test
    public void shouldLoadFromReader() {
        String content = "[urls]\n/path/** = handler1\n";
        Ini ini = new Ini();
        ini.load(new StringReader(content));
        assertNotNull(ini.getSection("urls"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenInputStreamIsNull() throws IOException {
        Ini ini = new Ini();
        ini.load((java.io.InputStream) null);
    }

    @Test
    public void shouldSkipComments() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("# comment\n; another comment\n[section]\nkey=value\n");
        assertNotNull(ini.getSection("section"));
        assertEquals("value", ini.getSectionProperty("section", "key"));
    }

    @Test
    public void shouldSkipEmptyLines() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("\n\n[section]\nkey=value\n\n");
        assertNotNull(ini.getSection("section"));
    }

    @Test
    public void shouldHandleDefaultSection() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("key=value\n");
        Ini.Section defaultSection = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        assertNotNull(defaultSection);
    }

    @Test
    public void shouldGetSectionNames() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s1]\nk=v\n[s2]\nk=v\n");
        Set<String> names = ini.getSectionNames();
        assertTrue(names.contains("s1"));
        assertTrue(names.contains("s2"));
    }

    @Test
    public void shouldGetSections() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s1]\nk=v\n");
        Collection<Ini.Section> sections = ini.getSections();
        assertFalse(sections.isEmpty());
    }

    @Test
    public void shouldReturnNullForNonExistentSection() {
        Ini ini = new Ini();
        assertNull(ini.getSection("nonexistent"));
    }

    @Test
    public void shouldAddSection() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("newSection");
        assertNotNull(section);
        assertEquals("newSection", section.getName());
    }

    @Test
    public void shouldReturnExistingSectionOnAdd() {
        Ini ini = new Ini();
        Ini.Section first = ini.addSection("test");
        Ini.Section second = ini.addSection("test");
        assertSame(first, second);
    }

    @Test
    public void shouldRemoveSection() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[test]\nk=v\n");
        Ini.Section removed = ini.removeSection("test");
        assertNotNull(removed);
        assertNull(ini.getSection("test"));
    }

    @Test
    public void shouldSetAndGetSectionProperty() {
        Ini ini = new Ini();
        ini.setSectionProperty("test", "key", "value");
        assertEquals("value", ini.getSectionProperty("test", "key"));
    }

    @Test
    public void shouldReturnNullForNonExistentProperty() {
        Ini ini = new Ini();
        assertNull(ini.getSectionProperty("nonexistent", "key"));
    }

    @Test
    public void shouldReturnDefaultForNonExistentProperty() {
        Ini ini = new Ini();
        assertEquals("default", ini.getSectionProperty("nonexistent", "key", "default"));
    }

    @Test
    public void shouldReturnActualValueOverDefault() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertEquals("v", ini.getSectionProperty("s", "k", "default"));
    }

    @Test
    public void shouldImplementMapMethods() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");

        assertTrue(ini.containsKey("s"));
        assertFalse(ini.containsKey("x"));
        assertTrue(ini.containsValue(ini.get("s")));
        assertNotNull(ini.get("s"));
        assertEquals(1, ini.size());

        Ini.Section newSection = new Ini().addSection("new");
        ini.put("new", newSection);
        assertTrue(ini.containsKey("new"));

        ini.remove("new");
        assertFalse(ini.containsKey("new"));
    }

    @Test
    public void shouldPutAll() {
        Ini ini1 = new Ini();
        ini1.addSection("s1");

        Ini ini2 = new Ini();
        ini2.putAll(ini1);
        assertTrue(ini2.containsKey("s1"));
    }

    @Test
    public void shouldClear() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        ini.clear();
        assertEquals(0, ini.size());
    }

    @Test
    public void shouldReturnKeySet() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        Set<String> keys = ini.keySet();
        assertTrue(keys.contains("s"));
    }

    @Test
    public void shouldReturnValues() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertFalse(ini.values().isEmpty());
    }

    @Test
    public void shouldReturnEntrySet() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertFalse(ini.entrySet().isEmpty());
    }

    @Test
    public void shouldTestEquals() throws EventHandleException {
        Ini ini1 = new Ini();
        ini1.load("[s]\nk=v\n");
        Ini ini2 = new Ini();
        ini2.load("[s]\nk=v\n");
        assertEquals(ini1, ini2);
        assertEquals(ini1.hashCode(), ini2.hashCode());
    }

    @Test
    public void shouldTestNotEquals() {
        Ini ini1 = new Ini();
        Ini ini2 = new Ini();
        ini2.addSection("s");
        assertNotEquals(ini1, ini2);
    }

    @Test
    public void shouldTestNotEqualsDifferentType() {
        Ini ini = new Ini();
        assertFalse(ini.equals("string"));
    }

    @Test
    public void shouldToString() {
        Ini ini = new Ini();
        assertEquals("<empty INI>", ini.toString());
    }

    @Test
    public void shouldToStringWithSections() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s1]\nk=v\n[s2]\nk=v\n");
        String str = ini.toString();
        assertTrue(str.contains("sections="));
    }

    @Test
    public void shouldDetectSectionHeader() {
        assertTrue(Ini.isSectionHeader("[test]"));
        assertFalse(Ini.isSectionHeader("test"));
        assertFalse(Ini.isSectionHeader(null));
    }

    @Test
    public void shouldGetSectionName() {
        assertEquals("test", Ini.getSectionName("[test]"));
        assertNull(Ini.getSectionName("test"));
    }

    // --- Section tests ---

    @Test
    public void shouldCreateSection() {
        Ini.Section section = new Ini().addSection("test");
        assertEquals("test", section.getName());
        assertTrue(section.isEmpty());
    }

    @Test
    public void shouldHandleNullSectionName() {
        // addSection with null defaults to DEFAULT_SECTION_NAME
        Ini ini = new Ini();
        Ini.Section section = ini.addSection(null);
        assertNotNull(section);
    }

    @Test
    public void shouldPutAndGetInSection() {
        Ini ini = new Ini();
        ini.load("[s]\nk1=v1\nk2=v2\n");
        Ini.Section section = ini.getSection("s");
        assertEquals("v1", section.get("k1"));
        assertEquals("v2", section.get("k2"));
    }

    @Test
    public void shouldRemoveFromSection() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        Ini.Section section = ini.getSection("s");
        section.remove("k");
        assertTrue(section.isEmpty());
    }

    @Test
    public void shouldPutAllInSection() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s");
        java.util.Map<String, String> map = new java.util.LinkedHashMap<>();
        map.put("k", "v");
        section.putAll(map);
        assertEquals("v", section.get("k"));
    }

    @Test
    public void shouldClearSection() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        Ini.Section section = ini.getSection("s");
        section.clear();
        assertTrue(section.isEmpty());
    }

    @Test
    public void shouldReturnSectionSize() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk1=v1\nk2=v2\n");
        assertEquals(2, ini.getSection("s").size());
    }

    @Test
    public void shouldReturnSectionContainsKey() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertTrue(ini.getSection("s").containsKey("k"));
        assertFalse(ini.getSection("s").containsKey("x"));
    }

    @Test
    public void shouldReturnSectionContainsValue() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertTrue(ini.getSection("s").containsValue("v"));
    }

    @Test
    public void shouldReturnSectionKeySet() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertTrue(ini.getSection("s").keySet().contains("k"));
    }

    @Test
    public void shouldReturnSectionValues() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertTrue(ini.getSection("s").values().contains("v"));
    }

    @Test
    public void shouldReturnSectionEntrySet() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertFalse(ini.getSection("s").entrySet().isEmpty());
    }

    @Test
    public void shouldTestSectionEquals() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        Ini ini2 = new Ini();
        ini2.load("[s]\nk=v\n");
        assertEquals(ini.getSection("s"), ini2.getSection("s"));
    }

    @Test
    public void shouldTestSectionNotEquals() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertFalse(ini.getSection("s").equals("string"));
    }

    @Test
    public void shouldTestSectionHashCode() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        Ini ini2 = new Ini();
        ini2.load("[s]\nk=v\n");
        assertEquals(ini.getSection("s").hashCode(), ini2.getSection("s").hashCode());
    }

    @Test
    public void shouldSectionToString() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s]\nk=v\n");
        assertEquals("s", ini.getSection("s").toString());
    }

    @Test
    public void shouldDefaultSectionToString() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("");
        assertEquals("<default>", section.toString());
    }

    @Test
    public void shouldDetectContinuation() {
        assertTrue(Ini.Section.isContinued("line\\"));
        assertFalse(Ini.Section.isContinued("line"));
        assertFalse(Ini.Section.isContinued(null));
        assertFalse(Ini.Section.isContinued(""));
    }

    @Test
    public void shouldDetectDoubleBackslashAsNotContinued() {
        assertFalse(Ini.Section.isContinued("line\\\\"));
    }

    @Test
    public void shouldSplitKeyValue() {
        String[] kv = Ini.Section.splitKeyValue("key=value");
        assertNotNull(kv);
        assertEquals("key", kv[0]);
        assertEquals("value", kv[1]);
    }

    @Test
    public void shouldSplitKeyValueWithColon() {
        String[] kv = Ini.Section.splitKeyValue("key:value");
        assertNotNull(kv);
        assertEquals("key", kv[0]);
        assertEquals("value", kv[1]);
    }

    @Test
    public void shouldSplitKeyValueWithWhitespace() {
        String[] kv = Ini.Section.splitKeyValue("key value");
        assertNotNull(kv);
        assertEquals("key", kv[0]);
        assertEquals("value", kv[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSplitKeyValueHasNoValue() {
        Ini.Section.splitKeyValue("keyonly");
    }

    @Test
    public void shouldReturnNullForNullSplitKeyValue() {
        assertNull(Ini.Section.splitKeyValue(null));
        assertNull(Ini.Section.splitKeyValue(""));
    }

    @Test
    public void shouldHandleMultipleSections() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[s1]\nk1=v1\n[s2]\nk2=v2\n");
        assertEquals("v1", ini.getSectionProperty("s1", "k1"));
        assertEquals("v2", ini.getSectionProperty("s2", "k2"));
    }

    @Test
    public void shouldHandleSectionWithNoContent() throws EventHandleException {
        Ini ini = new Ini();
        ini.load("[empty]\n");
        // Empty sections are not stored
        assertNull(ini.getSection("empty"));
    }

    @Test
    public void shouldHandleCopyConstructorSection() {
        Ini ini = new Ini();
        ini.addSection("test").put("key", "value");
        Ini copy = new Ini(ini);
        assertEquals("value", copy.getSectionProperty("test", "key"));
        // Modify original, copy should not change
        ini.getSection("test").put("key", "changed");
        assertEquals("value", copy.getSectionProperty("test", "key"));
    }
}
