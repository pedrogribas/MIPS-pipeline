import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A classe {@code InstructionScheduler} é responsável por agendar e resolver conflitos em um pipeline MIPS,
 * aplicando diferentes estratégias de resolução, como bolha, adiantamento e reordenamento.
 */

public class InstructionScheduler {
    private String filepathAnswer;
    private List<Instruction> instructions;
    private int resolutionStrategy; // 0 - Bolha, 1 - Reordenamento, 2 - Adiantamento, 3 - Melhor opção
    private boolean[] processedInstructions; // Para rastrear quais instruções já foram processadas

    /**
     * Cria uma instância de {@code InstructionScheduler}.
     *
     * @param instructions      A lista de instruções a ser agendada e otimizada.
     * @param resolutionStrategy A estratégia de resolução a ser usada (0 - Bolha, 1 - Reordenamento, 2 - Adiantamento, 3 - Melhor opção).
     * @param filepath          O caminho do arquivo onde o resultado será salvo.
     * @throws IllegalArgumentException Se a lista de instruções for nula.
     */

    public InstructionScheduler(List<Instruction> instructions, int resolutionStrategy, String filepath) {
        this.filepathAnswer = filepath;
        if (instructions == null) {
            throw new IllegalArgumentException("A lista de instruções não pode ser nula");
        }
        this.instructions = new ArrayList<>(instructions); // Cria uma cópia da lista para evitar modificações externas
        this.resolutionStrategy = resolutionStrategy;
        this.processedInstructions = new boolean[instructions.size()]; // Inicializa o array para marcar as instruções
    }

    /**
     * Verifica se há dependências RAW entre duas instruções.
     *
     * @param i O índice da primeira instrução.
     * @param j O índice da segunda instrução.
     * @return {@code true} se houver uma dependência RAW; {@code false} caso contrário.
     */

    public boolean checkDependencies(int i, int j) {
        Instruction instr1 = instructions.get(i);
        Instruction instr2 = instructions.get(j);

        // Verifica se há uma dependência RAW
        if (hasRAWDependency(instr1, instr2)) {
            System.out.println("Dependência RAW detectada entre instruções " + (i + 1) + " e " + (j + 1));
            return true; // Retorna true se houver uma dependência RAW
        }

        return false;
    }

    /**
     * Resolve os conflitos entre instruções de acordo com a estratégia definida.
     */

    public void resolve() {
        if (instructions == null) {
            throw new IllegalStateException("A lista de instruções não foi inicializada");
        }

        List<Instruction> newInstructions = new ArrayList<>(); // Lista para armazenar as novas instruções

        for (int i = 0; i < instructions.size(); i++) {
            if (processedInstructions[i]) {
                continue; // Pula se a instrução já foi processada
            }

            Instruction instr1 = instructions.get(i);
            newInstructions.add(instr1); // Adiciona a instrução original

            for (int j = i + 1; j < instructions.size(); j++) {
                if (checkDependencies(i, j)) {
                    // Aplica a resolução de acordo com a estratégia escolhida
                    applyResolution(i, j, newInstructions);

                    // Marca as instruções como processadas
                    processedInstructions[i] = true;
                    processedInstructions[j] = true;

                    // Sai do loop para avançar para a próxima instrução
                    break;
                }
            }
        }

        // Atualiza a lista de instruções com a nova lista
        instructions = new ArrayList<>(newInstructions);

        // Escreve o resultado final no arquivo de saída
        writeInstructionsToFile(filepathAnswer + "_RESULTADO");
    }

    /**
     * Verifica se há uma dependência RAW entre duas instruções.
     *
     * @param instr1 A primeira instrução.
     * @param instr2 A segunda instrução.
     * @return {@code true} se houver uma dependência RAW; {@code false} caso contrário.
     */

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

    /**
     * Aplica a resolução de conflitos de acordo com a estratégia definida.
     *
     * @param i               O índice da primeira instrução.
     * @param j               O índice da segunda instrução.
     * @param newInstructions A lista de instruções a ser atualizada com a resolução aplicada.
     */

    private void applyResolution(int i, int j, List<Instruction> newInstructions) {
        switch (resolutionStrategy) {
            case 0: // Bolha
                applyBubble(i, newInstructions);
                break;
            case 1: // Reordenamento
                System.out.println("Reordenamento ainda não implementado");
                break;
            case 2: // Adiantamento
                if (canApplyForwarding(i, j)) {
                    applyForwarding(i, j, newInstructions);
                } else {
                    applyBubble(i, newInstructions);
                }
                break;
            case 3: // Melhor opção
                if (canApplyForwarding(i, j)) {
                    applyForwarding(i, j, newInstructions);
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

    /**
     * Verifica se é possível aplicar forwarding entre duas instruções.
     *
     * @param i O índice da primeira instrução.
     * @param j O índice da segunda instrução.
     * @return {@code true} se o forwarding puder ser aplicado; {@code false} caso contrário.
     */

    private boolean canApplyForwarding(int i, int j) {
        Instruction instr1 = instructions.get(i);
        Instruction instr2 = instructions.get(j);

        // Se a primeira instrução é R-type e a segunda depende do resultado
        if (instr1 instanceof RType && instr2 instanceof RType) {
            RType r1 = (RType) instr1;
            RType r2 = (RType) instr2;
            // Se o registrador destino da primeira é igual a um dos registradores fonte da segunda
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

    /**
     * Aplica forwarding entre duas instruções, adicionando uma bolha (NOP) se necessário.
     *
     * @param i               O índice da primeira instrução.
     * @param j               O índice da segunda instrução.
     * @param newInstructions A lista de instruções a ser atualizada.
     */

    private void applyForwarding(int i, int j, List<Instruction> newInstructions) {
        // Adiciona a instrução i à nova lista
        newInstructions.add(instructions.get(i));
        // Adiciona um NOP se a instrução j depende da instrução i
        newInstructions.add(new NopType());
        // Adiciona a instrução j à nova lista
        newInstructions.add(instructions.get(j));
    }

    /**
     * Adiciona uma bolha (NOP) após uma instrução para resolver conflitos.
     *
     * @param i               O índice da instrução.
     * @param newInstructions A lista de instruções a ser atualizada.
     */

    private void applyBubble(int i, List<Instruction> newInstructions) {
        // Adiciona a instrução atual e uma bolha (NOP)
        newInstructions.add(instructions.get(i)); // Adiciona a instrução original
        newInstructions.add(new NopType()); // Adiciona uma instrução NOP (bolha)
    }

    /**
     * Verifica se é seguro reordenar duas instruções sem causar dependências.
     *
     * @param i O índice da primeira instrução.
     * @param j O índice da segunda instrução.
     * @return {@code true} se for seguro reordenar; {@code false} caso contrário.
     */

    private boolean canReorder(int i, int j) {
        Instruction instr1 = instructions.get(i);
        Instruction instr2 = instructions.get(j);

        // Verificar RAW
        if (hasRAWDependency(instr1, instr2)) {
            return false; // Não pode reordenar devido a RAW
        }

        // Verificar WAR
        if (hasWARDependency(instr1, instr2)) {
            return false; // Não pode reordenar devido a WAR
        }

        // Verificar WAW
        if (hasWAWDependency(instr1, instr2)) {
            return false; // Não pode reordenar devido a WAW
        }

        // Se nenhuma dependência for encontrada, é seguro reordenar
        return true;
    }

    /**
     * Verifica se há uma dependência WAR entre duas instruções.
     *
     * @param instr1 A primeira instrução.
     * @param instr2 A segunda instrução.
     * @return {@code true} se houver uma dependência WAR; {@code false} caso contrário.
     */

    private boolean hasWARDependency(Instruction instr1, Instruction instr2) {
        return instr1.getRd() != null && instr2.getRs() != null && instr1.getRd().equals(instr2.getRs());
    }

    /**
     * Verifica se há uma dependência WAW entre duas instruções.
     *
     * @param instr1 A primeira instrução.
     * @param instr2 A segunda instrução.
     * @return {@code true} se houver uma dependência WAW; {@code false} caso contrário.
     */

    private boolean hasWAWDependency(Instruction instr1, Instruction instr2) {
        return instr1.getRd() != null && instr2.getRd() != null && instr1.getRd().equals(instr2.getRd());
    }

    /**
     * Adiciona as instruções ao arquivo de saída.
     *
     * @param filename O nome do arquivo onde as instruções serão salvas.
     */

    private void writeInstructionsToFile(String filename) {
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (parentDir.mkdirs()) {
                System.out.println("Diretórios criados: " + parentDir.getAbsolutePath());
            } else {
                System.err.println("Falha ao criar diretórios: " + parentDir.getAbsolutePath());
                return;
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Instruction instr : instructions) {
                writer.write(formatInstruction(instr));
                writer.newLine();
            }
            System.out.println("Instruções escritas no arquivo " + filename);
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + filename);
            System.err.println("Mensagem de erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Formata uma instrução para a saída no arquivo.
     *
     * @param instr A instrução a ser formatada.
     * @return A representação formatada da instrução.
     */
    
    private String formatInstruction(Instruction instr) {
        if (instr instanceof RType) {
            RType r = (RType) instr;
            return String.format("%s %s, %s, %s", r.getOp(), r.getRd(), r.getRs(), r.getRt());
        } else if (instr instanceof IType) {
            IType i = (IType) instr;
            return String.format("%s %s, %s, %s", i.getOp(), i.getRt(), i.getRs(), i.getImmediate());
        } else if (instr instanceof NopType) {
            return "NOP";
        }
        return instr.toString();
    }
}
