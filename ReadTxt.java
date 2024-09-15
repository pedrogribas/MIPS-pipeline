import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ReadTxt {
    public ReadTxt() {
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
                String[] values = line.split("[\\s,\\t\\(\\)]+");
                for (String value : values) {
                    value = value.trim();
                }
                validateInstruction(values[0]);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Instruction validateInstruction(String instruction) {
        switch (instruction) {
            case "add", "sub", "addu", "subu", "and", "xor", "or", "nor", "slt", "sltu", "sra", "sllv", "srlv", "srav", "mfhi", "mthi", "mflo", "mtlo", "mult", "multu", "div", "divu", "jr", "sll", "srl", "jalr" -> {
                System.out.println("TIPO R");
                return new RType();
            }
            case "lb", "lw", "lh", "lbu", "lhu", "lwr", "lwl", "sw", "sb", "sh", "swl", "swr", "btlz", "bltzal", "bgezal", "beq", "bne", "blez", "bgtz" -> {
                System.out.println("TIPO I");
                return new IType();
            }
            case "addi", "addiu", "slti", "sltiu", "andi", "ori", "xori", "lui", "bltz", "bgez" -> {
                System.out.println("TIPO J");
                return new JType();
            }
            case "j", "jal" -> {
                System.out.println("TIPO ESPECIAL");
                return new Especial();
            }
            default -> throw new UnsupportedOperationException("Invalid instruction: " + instruction);
        }
    }
    }