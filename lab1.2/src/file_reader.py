import sys
from pathlib import Path

class TextFileReader:
    def read_file(self, file_path: str) -> str | None:
        full_path = Path(file_path)
        if not full_path.exists():
            return None
        if full_path.stat().st_size == 0:
            return ""
        return full_path.read_text(encoding='utf-8')
    def main(self):
        for file_path in sys.argv[1:]:
            content = self.read_file(file_path)
            if content is None:
                print("file oldsongui.")
            elif content == "":
                print("file hooson baina.")
            else:
                print(content)

if __name__ == "__main__":
    reader = TextFileReader()
    reader.main()
