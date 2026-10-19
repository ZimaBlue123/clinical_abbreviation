"""
Clinical Abbreviation Data Pipeline
Syncs, cleans, and validates clinical abbreviation data from CSV to Android assets JSON.
"""

import csv
import json
import os
import re
import sys
from typing import Dict, List, Tuple


def clean_text(text: str) -> str:
    if not text:
        return ""
    # Normalize unicode non-breaking spaces and non-breaking hyphens
    text = text.replace("\xa0", " ").replace("\u2011", "-").replace("\r", "")
    # Remove repeated whitespace and newlines
    text = re.sub(r"\s+", " ", text)
    return text.strip()


def parse_csv_file(csv_path: str) -> Tuple[List[Dict[str, str]], List[str]]:
    if not os.path.exists(csv_path):
        raise FileNotFoundError(f"Source CSV file not found: {csv_path}")

    items: List[Dict[str, str]] = []
    warnings: List[str] = []

    with open(csv_path, "r", encoding="utf-8-sig", errors="replace") as f:
        reader = csv.reader(f)
        try:
            header = next(reader)
        except StopIteration:
            return items, ["CSV file is empty"]

        # Expected format: Name, 全称, 缩写, 译文
        for idx, row in enumerate(reader, start=2):
            if not row or len(row) < 3:
                continue

            full_form = clean_text(row[1]) if len(row) > 1 else ""
            abbr = clean_text(row[2]) if len(row) > 2 else ""
            chinese = clean_text(row[3]) if len(row) > 3 else ""

            if not abbr and not full_form and not chinese:
                continue

            if not abbr:
                warnings.append(f"Row {idx}: missing abbreviation for '{full_form}'")

            items.append({
                "abbreviation": abbr,
                "full_form": full_form,
                "chinese": chinese
            })

    return items, warnings


def export_to_json(items: List[Dict[str, str]], json_path: str) -> None:
    os.makedirs(os.path.dirname(os.path.abspath(json_path)), exist_ok=True)
    with open(json_path, "w", encoding="utf-8") as f:
        json.dump(items, f, ensure_ascii=False, indent=2)


def main() -> int:
    base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    default_csv = os.path.join(base_dir, "abbreviation list.csv")
    default_json = os.path.join(base_dir, "app", "src", "main", "assets", "abbreviations.json")

    csv_path = sys.argv[1] if len(sys.argv) > 1 else default_csv
    json_path = sys.argv[2] if len(sys.argv) > 2 else default_json

    print(f"[Pipeline] Source CSV : {csv_path}")
    print(f"[Pipeline] Target JSON: {json_path}")

    items, warnings = parse_csv_file(csv_path)
    for w in warnings:
        print(f"[Pipeline] WARNING: {w}")

    export_to_json(items, json_path)
    print(f"[Pipeline] Successfully synced {len(items)} abbreviations to {json_path}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
