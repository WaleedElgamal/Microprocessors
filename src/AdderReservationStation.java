public class AdderReservationStation extends ReservationStation{
    private static int count=1;

    public AdderReservationStation(){
        super();
        this.setTag("A" + count++);
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
