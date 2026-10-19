package com.example.clinical_abbreviation;

import com.google.gson.Gson;
import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testJsonParsing() throws Exception {
        File file = new File("src/main/assets/abbreviations.json");
        assertTrue("Asset file must exist", file.exists());

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            AbbreviationModel[] array = gson.fromJson(reader, AbbreviationModel[].class);
            assertNotNull("Parsed array must not be null", array);
            assertTrue("Should contain 320 items", array.length >= 318);
            
            AbbreviationModel first = array[0];
            assertNotNull(first.getAbbreviation());
            assertFalse(first.getAbbreviation().isEmpty());
            assertNotNull(first.getFullForm());
            assertNotNull(first.getChinese());
            System.out.println("First item: " + first);
            System.out.println("Total items: " + array.length);
        }
    }
}