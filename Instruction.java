public abstract class Instruction {
    protected String op;

    public Instruction(String[] instruction) {
        // Inicialização comum
        if (instruction.length > 0) {
            this.op = instruction[0];
        }
    }

    public String getOp() {
        return op;
    }

    // Métodos que podem retornar nulo
    public String getRs() {
        return null;
    }

    public String getRt() {
        return null;
    }

    public String getRd() {
        return null;
    }
}
