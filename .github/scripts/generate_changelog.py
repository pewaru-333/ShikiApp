import sys
import subprocess


def get_previous_tag():
    try:
        command = "git tag --sort=-v:refname"
        result = subprocess.run(
            command, shell=True, capture_output=True, text=True, check=True, encoding='utf-8'
        )

        tags = [tag for tag in result.stdout.strip().split('\n') if tag]

        if len(tags) < 2:
            return None

        previous_tag = tags[1]
        print(f"Найден предыдущий тег: {previous_tag}", file=sys.stderr)
        return previous_tag

    except subprocess.CalledProcessError as e:
        print(f"Ошибка при получении тегов: {e.stderr}", file=sys.stderr)
        return None


def main():
    version_code = sys.argv[1]
    version_name = sys.argv[2]
    repo = sys.argv[3]

    changelog_path = f"fastlane/metadata/android/ru/changelogs/{version_code}.txt"
    try:
        with open(changelog_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
    except FileNotFoundError:
        sys.exit(1)

    transformed_lines = []
    for line in lines:
        if line.strip().startswith(('-', '—')):
            transformed_lines.append(line.strip().replace("-", "*").replace("—", "*"))
        else:
            transformed_lines.append(line)

    changelog_content = "\n".join(transformed_lines).strip()
    release_body = f"## Основные изменения\n\n{changelog_content}"
    previous_tag = get_previous_tag()

    if previous_tag:
        compare_link = f"\n\n**Полный список изменений**: https://github.com/{repo}/compare/{previous_tag}...{version_name}"
        release_body += compare_link

    print(release_body)


if __name__ == "__main__":
    main()