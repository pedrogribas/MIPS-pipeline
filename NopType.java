public class NopType extends Instruction {

  // Construtor simples que chama o construtor da classe pai
  public NopType() {
    super(new String[] { "NOP" }); // O array de instruções terá apenas "NOP"
  }

  @Override
  public String toString() {
    return "NOP";
  }
}
