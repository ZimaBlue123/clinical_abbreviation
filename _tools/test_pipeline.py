import os
import sys
import unittest

# Ensure _tools is accessible
sys.path.insert(0, os.path.dirname(__file__))
from sync_abbreviations import clean_text, parse_csv_file


class TestDataPipeline(unittest.TestCase):

    def test_clean_text(self):
        self.assertEqual(clean_text(" AC \xa0 "), "AC")
        self.assertEqual(clean_text("a\u2011b"), "a-b")
        self.assertEqual(clean_text("multi   space"), "multi space")
        self.assertEqual(clean_text(""), "")

    def test_parse_csv(self):
        base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
        csv_path = os.path.join(base_dir, "abbreviation list.csv")
        if os.path.exists(csv_path):
            items, warnings = parse_csv_file(csv_path)
            self.assertGreaterEqual(len(items), 300)
            self.assertTrue(any(item["abbreviation"] == "AC" for item in items))


if __name__ == "__main__":
    unittest.main()
