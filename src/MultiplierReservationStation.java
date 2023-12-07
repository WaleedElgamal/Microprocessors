public class MultiplierReservationStation extends ReservationStation{

    private static int count=1;

    public MultiplierReservationStation(){
        super();
        this.setTag("M" + count++);
    }

    public String toString(){
        String str= getTag() + " " + isBusy() + " ";
        if(getOpcode().equals("")){
            return str;
        }
        str += getOpcode() + "   ";
        int val1 = getSourceValue1();
        int val2 = getSourceValue2();
        str += val1 + "     " + val2 + "     " + getSourceReg1() + "    " + getSourceReg2() +  "    " + getMemoryAddress();
        return str;
    }

}
