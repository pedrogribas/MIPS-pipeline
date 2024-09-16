public class IType extends Instruction {
    private String rs;
    private String rt;
    private String immediate;

    public IType(String[] instruction) {
        super(instruction);

        // Verificar o comprimento do array para evitar exceções
        if (instruction.length >= 4) {
            // Atribuir valores com base em posição
            this.rs = instruction[1].trim(); // Registrador base
            this.rt = instruction[2].trim(); // Registrador de destino ou valor imediato
            this.immediate = instruction[3].trim(); // Valor imediato ou deslocamento
            var aux = immediate;
            // Verificar se `immediate` é numérico
            if (isNumeric(immediate)) {
                // Se `immediate` é numérico, então ele é o valor imediato
                this.immediate = immediate;
            } else {
                // Se `rt` é numérico, então `rt` é um valor imediato
                if (isNumeric(rt)) {
                    this.immediate = rt;
                    this.rt = aux; // `rt` não é um registrador válido aqui
                } else {
                    // Caso onde o `immediate` é uma label ou outra forma não numérica
                    this.immediate = "";
                }
            }
        } else {
            // Mensagem de erro para instrução mal formatada
            System.err.println("Instrução I-Type mal formatada: " + String.join(", ", instruction));
            this.rs = "";
            this.rt = "";
            this.immediate = "";
        }
    }

    @Override
    public String toString() {
        return "IType{" +
                "op='" + op + '\'' +
                ", rs='" + rs + '\'' +
                ", rt='" + rt + '\'' +
                ", immediate='" + immediate + '\'' +
                '}';
    }

    // Função auxiliar para verificar se uma string é um número
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getRs() {
        return rs;
    }

    @Override
    public String getRt() {
        return rt;
    }

    public String getImmediate() {
        return immediate;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }
}
