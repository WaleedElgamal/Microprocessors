public class Instruction {
    private String strType;
    private int iDestinationRegister;
    private int iSourceRegister1;
    private int iSourceRegister2;
    private int iImmediateValue;

    private String label;

    public Instruction(){

    }
    public Instruction(String type, int dest, int source1, int source2){
        strType = type;
        iDestinationRegister = dest;
        iSourceRegister1 = source1;
        iSourceRegister2 = source2;

        //in case it is ADDI or SUBI, source2 will be the immediate value
        iImmediateValue = source2;
    }

    public Instruction(String type, int dest, int immediate){
        strType = type;
        iDestinationRegister = dest;
        iImmediateValue = immediate;
    }

    public Instruction(String type, int dest, String label){
        strType = type;
        iDestinationRegister = dest;
        this.label = label;
    }


    public String toString(){
        String res =  strType +" ";
        char register;
        if (strType.equals("ADDI") || strType.equals("SUBI")){
            register = 'R';
            res += register +  "" + iDestinationRegister + " " +
                    register + iSourceRegister1 + " " +
                    iImmediateValue;
        }
        else if (strType.equals("BNEZ")){
            register = 'R';
            res += register + "" +iDestinationRegister + " " +
                    label;
        }
        else if (strType.equals("L.D") || strType.equals("S.D")){
            register = 'F';
            res += register + "" +iDestinationRegister + " " +

                    iImmediateValue;
        }
        else{
            register='F';
            res += register + ""  +iDestinationRegister + " " +
                    register + iSourceRegister1 + " " +
                    register + iSourceRegister2;
        }
        return res;
    }

    public String getType() {
        return strType;
    }

    public void setType(String strType) {
        this.strType = strType;
    }

    public int getDestinationRegister() {
        return iDestinationRegister;
    }

    public void setDestinationRegister(int iDestinationRegister) {
        this.iDestinationRegister = iDestinationRegister;
    }

    public int getSourceRegister1() {
        return iSourceRegister1;
    }

    public void setSourceRegister1(int iSourceRegister1) {
        this.iSourceRegister1 = iSourceRegister1;
    }

    public int getSourceRegister2() {
        return iSourceRegister2;
    }

    public void setSourceRegister2(int iSourceRegister2) {
        this.iSourceRegister2 = iSourceRegister2;
    }

    public int getImmediateValue() {
        return iImmediateValue;
    }

    public void setImmediateValue(int iImmediateValue) {
        this.iImmediateValue = iImmediateValue;
    }

    public String getLabel() {
        return label;
    }
}
