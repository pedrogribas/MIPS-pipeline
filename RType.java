public class RType extends Instruction {
    private String rs;
    private String rt;
    private String rd;

    public RType(String[] instruction) {
        super(instruction);

        // Verificar o comprimento do array para evitar exceções
        if (instruction.length >= 4) {
            this.rd = instruction[1].trim(); // Registrador de destino
            this.rs = instruction[2].trim(); // Primeiro registrador fonte
            this.rt = instruction[3].trim(); // Segundo registrador fonte ou valor imediato

            // Verificar se `rt` é um número (no caso de `sll`)
            if (isNumeric(rt)) {
                // No caso de `sll`, `rt` é na verdade `shamt` (valor imediato)
                this.rt = "shamt=" + rt; // Tratar como um valor imediato
            }
        } else {
            // Mensagem de erro para instrução mal formatada
            System.err.println("Instrução R-Type mal formatada: " + String.join(", ", instruction));
            this.rd = "";
            this.rs = "";
            this.rt = "";
        }
    }

    @Override
    public String toString() {
        return "RType{" +
                "op='" + op + '\'' +
                ", rs='" + rs + '\'' +
                ", rt='" + rt + '\'' +
                ", rd='" + rd + '\'' +
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
    public String getRd() {
        return rd;
    }

    public void setRd(String rd) {
        this.rd = rd;
    }

    @Override
    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    @Override
    public String getRt() {
        return rt;
    }

}
