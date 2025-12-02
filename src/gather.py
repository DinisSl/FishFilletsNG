import os

# Directory with your .java files
input_dirA = "C:\\Users\\user\\Desktop\\Coding\\IdeaProjects\\FishFilletsNG_DinisLopes123007_BrunoMarques122476\\src"
output_file = "Combined.java"

with open(output_file, "w", encoding="utf-8") as outfile:
    for root, _, files in os.walk(input_dirA):  # walks through all subfolders too
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                with open(file_path, "r", encoding="utf-8") as infile:
                    outfile.write(f"// ===== File: {file_path} =====\n")
                    outfile.write(infile.read())
                    outfile.write("\n\n")

print(f"✅ Combined all .java files into '{output_file}'")
