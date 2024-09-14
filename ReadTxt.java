import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ReadTxt {
    private List<String> readInstructions;
    private List<String> writeInstructions;
    private List<String> readWriteInstructions;
    private List<String> otherThigs;

    public ReadTxt() {
        readInstructions = new ArrayList<>();
        writeInstructions = new ArrayList<>();
        readWriteInstructions = new ArrayList<>();
        otherThigs = new ArrayList<>();
    }

    public void read(String filepath) {
        File arquivo = new File(filepath);
        if (!arquivo.exists()) {
            System.out.println("arquivo não existe");
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(arquivo));
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> tempReadInstructions = new ArrayList<>();
                List<String> tempWriteInstructions = new ArrayList<>();
                List<String> tempReadWriteInstructions = new ArrayList<>();
                List<String> tempOtherThigs = new ArrayList<>();

                String[] values = line.split("[\\s,\\t\\(\\)]+");
                for (String value : values) {
                    value = value.trim();
                    if (isReadInstruction(value)) {
                        tempReadInstructions.add(value);
                    } else if (isWriteInstruction(value)) {
                        tempWriteInstructions.add(value);
                    } else if (isReadWriteInstruction(value)) {
                        tempReadWriteInstructions.add(value);
                    }
                    else{
                        tempOtherThigs.add(value);
                    }
                }

                System.out.println("Instructions for line: " + line);
                System.out.println("Read Instructions:");
                for (String value : tempReadInstructions) {
                    System.out.println("  " + value);
                }
                System.out.println("Write Instructions:");
                for (String value : tempWriteInstructions) {
                    System.out.println("  " + value);
                }
                System.out.println("Read/Write Instructions:");
                for (String value : tempReadWriteInstructions) {
                    System.out.println("  " + value);
                }
                System.out.println("Outras coisas:");
                for (String value : tempOtherThigs) {
                    System.out.println("  " + value);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isReadInstruction(String instruction) {
        String[] readInstructionsArray = {"lb", "lh", "lwl", "lw", "lbu", "lhu", "lwr", "mfhi", "mflo"};
        for (String readInstruction : readInstructionsArray) {
            if (readInstruction.equals(instruction)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWriteInstruction(String instruction) {
        String[] writeInstructionsArray = {"sw", "sb", "sh", "swl", "swr", "mthi", "mtlo"};
        for (String writeInstruction : writeInstructionsArray) {
            if (writeInstruction.equals(instruction)) {
                return true;
            }
        }
        return false;
    }

    private boolean isReadWriteInstruction(String instruction) {
        String[] readWriteInstructionsArray = {"add", "addu", "sub", "subu", "and", "or", "xor", "nor", "slt", "sltu", "addi", "addiu", "slti", "sltiu", "andi", "ori", "xori", "lui", "sll", "srl", "sra", "sllv", "srlv", "srav", "mult", "multu", "div", "divu", "jr", "bltz", "bgez", "bltzal", "bgezal", "j", "jal", "jalr"};
        for (String readWriteInstruction : readWriteInstructionsArray) {
            if (readWriteInstruction.equals(instruction)) {
                return true;
            }
        }
        return false;
    }
}