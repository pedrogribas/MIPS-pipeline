public class JType extends Instruction {
    private String target;

    public JType(String[] instruction) {
        super(instruction);

        // Verificar o comprimento do array para evitar exceções
        if (instruction.length >= 2) {
            this.target = instruction[1].trim();  // Endereço ou rótulo de destino
        } else {
            // Mensagem de erro para instrução mal formatada
            System.err.println("Instrução J-Type mal formatada: " + String.join(", ", instruction));
            this.target = "";
        }
    }

    @Override
    public String toString() {
        return "JType{" +
                "op='" + op + '\'' +
                ", target='" + target + '\'' +
                '}';
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
