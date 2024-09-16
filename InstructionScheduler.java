import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstructionScheduler {
    private String filepathAnswer;
    private List<Instruction> instructions;
    private int resolutionStrategy; // 0 - Bolha, 1 - Reordenamento, 2 - Adiantamento, 3 - Melhor opção
    private boolean[] processedInstructions; // Para rastrear quais instruções já foram processadas

    public InstructionScheduler(List<Instruction> instructions, int resolutionStrategy, String filepath) {
        filepathAnswer = filepath;
        if (instructions == null) {
            throw new IllegalArgumentException("A lista de instruções não pode ser nula");
        }
        this.instructions = new ArrayList<>(instructions); // Cria uma cópia da lista para evitar modificações externas
        this.resolutionStrategy = resolutionStrategy;
        this.processedInstructions = new boolean[instructions.size()]; // Inicializa o array para marcar as instruções
    }
    public void checkDependencies() {
        if (instructions == null) {
            throw new IllegalStateException("A lista de instruções não foi inicializada");
        }
    
        boolean changesMade;
        List<Instruction> newInstructions;
    
        do {
            changesMade = false;
            newInstructions = new ArrayList<>();
            int i = 0;
    
            while (i < instructions.size()) {
                Instruction instr1 = instructions.get(i);
                newInstructions.add(instr1); // Adiciona a instrução original
    
                boolean hasDependency = false;
                int j;
                
                // Verifica dependências para as instruções subsequentes
                for (j = i + 1; j < instructions.size(); j++) {
                    Instruction instr2 = instructions.get(j);
                    if (hasRAWDependency(instr1, instr2)) {
                        System.out.println("Dependência RAW detectada entre instruções " + (i + 1) + " e " + (j + 1));
                        applyBubble(i, newInstructions); // Adiciona um NOP
                        changesMade = true;
                        hasDependency = true;
                        break; // Sai do loop para evitar múltiplas bolhas para a mesma dependência
                    }
                }
    
                if (hasDependency) {
                    // Adiciona a bolha e avança o índice para pular a instrução original e a bolha
                    i = j + 1;
                } else {
                    // Avança normalmente se não houver dependência
                    i++;
                }
            }
    
            instructions = new ArrayList<>(newInstructions); // Atualiza a lista de instruções
        } while (changesMade);
    
        writeInstructionsToFile(filepathAnswer + "_RESULTADO");
    }
    
    private boolean hasRAWDependency(Instruction instr1, Instruction instr2) {
        if (instr1 instanceof RType && instr2 instanceof RType) {
            RType r1 = (RType) instr1;
            RType r2 = (RType) instr2;
            return r1.getRd() != null && (r1.getRd().equals(r2.getRs()) || r1.getRd().equals(r2.getRt()));
        } else if (instr1 instanceof RType && instr2 instanceof IType) {
            RType r1 = (RType) instr1;
            IType i2 = (IType) instr2;
            return r1.getRd() != null && r1.getRd().equals(i2.getRs());
        } else if (instr1 instanceof IType && instr2 instanceof RType) {
            IType i1 = (IType) instr1;
            RType r2 = (RType) instr2;
            return i1.getRt() != null && (i1.getRt().equals(r2.getRs()) || i1.getRt().equals(r2.getRt()));
        } else if (instr1 instanceof IType && instr2 instanceof IType) {
            IType i1 = (IType) instr1;
            IType i2 = (IType) instr2;
            return i1.getRt() != null && i1.getRt().equals(i2.getRs());
        }
        return false;
    }
    

    private void applyResolution(int i, int j, List<Instruction> newInstructions) {
        switch (resolutionStrategy) {
            case 0: // Bolha
                applyBubble(i, newInstructions);
                break;
            case 1: // Reordenamento
                System.out.println("Reordenamento ainda não implementado");
                break;
            case 2: // Adiantamento
                System.out.println("Adiantamento ainda não implementado");
                break;
            case 3: // Melhor opção
                if (canApplyForwarding(i, j)) {
                    System.out.println("Aplicando adiantamento entre instruções " + (i + 1) + " e " + (j + 1));
                } else if (canReorder(i, j)) {
                    System.out.println("Aplicando reordenamento entre instruções " + (i + 1) + " e " + (j + 1));
                } else {
                    applyBubble(i, newInstructions);
                }
                break;
            default:
                throw new IllegalArgumentException("Estratégia de resolução inválida: " + resolutionStrategy);
        }
    }
    private void applyBubble(int i, List<Instruction> newInstructions) {
        newInstructions.add(new NopInstruction()); // Adiciona a bolha (NOP)
    }
    
    private void writeInstructionsToFile(String filename) {
        File file = new File(filename);

        // Certifica-se de que o diretório pai existe e cria-o se necessário
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (parentDir.mkdirs()) {
                System.out.println("Diretórios criados: " + parentDir.getAbsolutePath());
            } else {
                System.err.println("Falha ao criar diretórios: " + parentDir.getAbsolutePath());
                return;
            }
        }

        // Tenta criar o BufferedWriter para escrever no arquivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Instruction instr : instructions) {
                writer.write(formatInstruction(instr));
                writer.newLine();
            }
            System.out.println("Instruções com bolhas escritas no arquivo " + filename);
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + filename);
            System.err.println("Mensagem de erro: " + e.getMessage());
            e.printStackTrace(); // Adiciona a pilha de chamadas para ajudar no diagnóstico
        }
    }

    private String formatInstruction(Instruction instr) {
        // Formata a instrução de acordo com o tipo
        if (instr instanceof RType) {
            RType r = (RType) instr;
            return String.format("%s %s, %s, %s", r.getOp(), r.getRd(), r.getRs(), r.getRt());
        } else if (instr instanceof IType) {
            IType i = (IType) instr;
            // Usa %s para o campo immediate, já que ele é sempre uma string
            return String.format("%s %s, %s, %s", i.getOp(), i.getRt(), i.getRs(), i.getImmediate());
        }
        return instr.toString();
    }
    

    private boolean canApplyForwarding(int i, int j) {
        // Implementar a lógica para verificar se o forwarding pode resolver o conflito
        return false;
    }

    private boolean canReorder(int i, int j) {
        // Implementar a lógica para verificar se o reordenamento pode ser feito sem
        // alterar a lógica do programa
        return false;
    }

    // Classe interna representando uma instrução NOP (No Operation)
    private static class NopInstruction extends Instruction {
        public NopInstruction() {
            super(new String[] { "nop" });
        }

        @Override
        public String toString() {
            return "NOP";
        }
    }
}
