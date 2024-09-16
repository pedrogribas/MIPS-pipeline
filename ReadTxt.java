import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ReadTxt {
    private List<Instruction> instructions;

    public ReadTxt() {
        this.instructions = new ArrayList<>();
    }

    public void read(String filepath, int strategy) {
        File arquivo = new File(filepath);
        if (!arquivo.exists()) {
            System.out.println("Arquivo não existe");
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(arquivo));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split("\\s+|,|\\(|\\)");
                List<String> cleanedValues = new ArrayList<>();
                for (String value : values) {
                    String trimmed = value.trim();
                    if (!trimmed.isEmpty()) {
                        cleanedValues.add(trimmed);
                    }
                }
                String[] cleanedArray = cleanedValues.toArray(new String[0]);
                Instruction instruction = validateInstruction(cleanedArray);
                instructions.add(instruction);
            }
            reader.close();

            // Agora, após a lista de instruções ser preenchida
            InstructionScheduler scheduler = new InstructionScheduler(instructions, strategy, filepath);
            scheduler.resolve(); // chama classe schedule
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Instruction validateInstruction(String[] instruction) {
        switch (instruction[0]) {
            case "add", "sub", "addu", "subu", "and", "xor", "or", "nor", "slt", "sltu", "sra", "sllv", "srlv", "srav",
                    "mfhi", "mthi", "mflo", "mtlo", "mult", "multu", "div", "divu", "jr", "sll", "srl", "jalr" -> {
                return new RType(instruction);
            }
            case "lb", "lw", "lh", "lbu", "lhu", "lwr", "lwl", "sw", "sb", "sh", "swl", "swr", "btlz", "bltzal",
                    "bgezal", "beq", "bne", "blez", "bgtz", "addi", "addiu", "slti", "sltiu", "andi", "ori", "xori",
                    "lui", "bltz", "bgez" -> {
                return new IType(instruction);
            }
            case "j", "jal" -> {
                return new JType(instruction);
            }
            case "NOP" -> {
                return new NopType();
            }
            default -> throw new UnsupportedOperationException("Invalid instruction: " + instruction[0]);
        }
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void printInstructions() {
        System.out.println("Lista de Instruções:");
        for (int i = 0; i < instructions.size(); i++) {
            System.out.println("Instrução " + (i + 1) + ": " + instructions.get(i));
        }
    }
}
