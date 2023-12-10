import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    private AdderReservationStation[] AdderReservationStations;
    private MultiplierReservationStation[] MultiplierReservationStations;
    private Register[] RegisterFile;

    private IntegerRegister[] IntegerRegisterFile;
    private double[] Cache;
    private LoadBuffer[] LoadBuffers;
    private static StoreBuffer[] StoreBuffers;
    private HashMap<String, Integer> hmInstructionCycles = new HashMap<>();

    private static HashMap<String, Integer> hmLabels = new HashMap<>();

    private static int i ;


    static int iClockCycle = 1;
    //    private Queue<String> WriteResult = new LinkedList<>();
    private PriorityQueue<String[]> WriteResult = new PriorityQueue<>(
            new Comparator<String[]>() {
                public int compare(String[] o1, String[] o2) {
                    if (Integer.parseInt(o1[1]) < Integer.parseInt(o2[1]))
                        return -1;
                    else if (Integer.parseInt(o1[1]) > Integer.parseInt(o2[1]))
                        return 1;
                    else
                        return 0;
                }
            }
    );

    public Main() {
        Cache = new double[10]; //any size and also load with any values
        for (int i = 0; i < Cache.length; i++) {
            Cache[i] = i;
        }
        RegisterFile = new Register[32];
        for (int i = 0; i < RegisterFile.length; i++) {
            RegisterFile[i] = new Register(i, i);
        }
        IntegerRegisterFile = new IntegerRegister[32];
        for (int i = 0; i < IntegerRegisterFile.length; i++) {
            IntegerRegisterFile[i] = new IntegerRegister(i, i);
        }

        hmInstructionCycles.put("ADD.D", -1);
        hmInstructionCycles.put("SUB.D", -1);
        hmInstructionCycles.put("MUL.D", -1);
        hmInstructionCycles.put("DIV.D", -1);

        hmInstructionCycles.put("ADDI", 1); //specified
        hmInstructionCycles.put("SUBI", 1); //should be one as well ? yes

        hmInstructionCycles.put("L.D", -1);
        hmInstructionCycles.put("S.D", -1);

        hmInstructionCycles.put("BNEZ", 1); //specified
    }

    public static boolean isInstruction(String str) {
        return str.equals("ADD.D") || str.equals("SUB.D") || str.equals("MUL.D") || str.equals("DIV.D") ||
                str.equals("ADDI") || str.equals("SUBI") || str.equals("L.D") || str.equals("S.D") ||
                str.equals("BNEZ");
    }

    public static void ParseProgram(ArrayList<Instruction> Program) throws IOException {
        // todo use correct path for instruction file, use relative src/
        File file = new File("src/Program.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int i = 1;
        while ((st = br.readLine()) != null) {
            // todo print value after parsing
            String[] stValues = st.split(" ");
            for (int j = 0; j < stValues.length; j++) {
                stValues[j] = stValues[j].toUpperCase();
            }
            Instruction newInstruction = new Instruction();
            if (isInstruction(stValues[0])) {
                String type = stValues[0];
                int dest = Integer.parseInt(stValues[1].substring(1));

                if (type.equals("ADD.D") || type.equals("SUB.D") || type.equals("MUL.D") || type.equals("DIV.D")) {
                    int source1 = Integer.parseInt(stValues[2].substring(1));
                    int source2 = Integer.parseInt(stValues[3].substring(1));
                    newInstruction = new Instruction(type, dest, source1, source2);
                } else if (type.equals("L.D") || type.equals("S.D")) {
                    int immediate = Integer.parseInt(stValues[2]);
                    newInstruction = new Instruction(type, dest, immediate);
                } else if (type.equals("ADDI") || type.equals("SUBI")) {
                    int source1 = Integer.parseInt(stValues[2].substring(1));
                    int immediate = Integer.parseInt(stValues[3]);
                    newInstruction = new Instruction(type, dest, source1, immediate);
                }
                else if (type.equals("BNEZ")) {
                    String label = stValues[2];
                    newInstruction = new Instruction(type, dest, label);
                }
            } else { // label
                hmLabels.put(stValues[0], i-1);
                String type = stValues[1];
                int dest = Integer.parseInt(stValues[2].substring(1));

                if (type.equals("ADD.D") || type.equals("SUB.D") || type.equals("MUL.D") || type.equals("DIV.D")) {
                    int source1 = Integer.parseInt(stValues[3].substring(1));
                    int source2 = Integer.parseInt(stValues[4].substring(1));
                    newInstruction = new Instruction(type, dest, source1, source2);
                } else if (type.equals("L.D") || type.equals("S.D")) {
                    int immediate = Integer.parseInt(stValues[3]);
                    newInstruction = new Instruction(type, dest, immediate);
                } else if (type.equals("ADDI") || type.equals("SUBI")) {
                    int source1 = Integer.parseInt(stValues[3].substring(1));
                    int immediate = Integer.parseInt(stValues[4]);
                    newInstruction = new Instruction(type, dest, source1, immediate);
                }
                else if (type.equals("BNEZ")) {
                    String label = stValues[2];
                    newInstruction = new Instruction(type, dest, label);
                }


            }
            Program.add(newInstruction);
            //System.out.println(newInstruction);
            i++;
        }
        //numOfInstructions = i; // storing the number of instructions in instruction file
        // will be used to calculate number of clock cycles
    }


    public static void main(String[] args) throws IOException {
        Main Tomasulo = new Main();
        ArrayList<Instruction> Program = new ArrayList<>();
        ParseProgram(Program);
        TreeSet<Integer> Executing = new TreeSet<>();
        System.out.println(Program);
        System.out.println(hmLabels);

        Scanner sc = new Scanner(System.in);

        //todo initialize register file

        System.out.println("Choose the number of adder reservation stations");
        int iNumAdderStations = sc.nextInt();
        Tomasulo.AdderReservationStations = new AdderReservationStation[iNumAdderStations];
        for (int i = 0; i < iNumAdderStations; i++) {
            Tomasulo.AdderReservationStations[i] = new AdderReservationStation();
        }

        System.out.println("Choose the number of multiplier reservation stations");
        int iNumMultiplierStations = sc.nextInt();
        Tomasulo.MultiplierReservationStations = new MultiplierReservationStation[iNumMultiplierStations];
        for (int i = 0; i < iNumMultiplierStations; i++) {
            Tomasulo.MultiplierReservationStations[i] = new MultiplierReservationStation();
        }

        //--------------------------------------------------------------------------------------------------

        System.out.println("Choose the number of load buffers");
        int iNumLoadBuffers = sc.nextInt();

        Tomasulo.LoadBuffers = new LoadBuffer[iNumLoadBuffers];
        for (int i = 0; i < iNumLoadBuffers; i++) {
            Tomasulo.LoadBuffers[i] = new LoadBuffer();
        }

        //--------------------------------------------------------------------------------------------------
        System.out.println("Choose the number of store buffers");
        int iNumStoreBuffers = sc.nextInt();

        Tomasulo.StoreBuffers = new StoreBuffer[iNumStoreBuffers];
        for (int i = 0; i < iNumStoreBuffers; i++) {
            Tomasulo.StoreBuffers[i] = new StoreBuffer();
        }

        //--------------------------------------------------------------------------------------------------


        for (String strInstruction : Tomasulo.hmInstructionCycles.keySet()) {
            if (!strInstruction.equals("ADDI") && !strInstruction.equals("BNEZ") && !strInstruction.equals("SUBI")) {
                System.out.println("Choose the latency of " + strInstruction + " instruction:");
                int iLatency = sc.nextInt();
                Tomasulo.hmInstructionCycles.replace(strInstruction, iLatency);
            }
        }

        //--------------------------------------------------------------------------------------------------


         i = 0;
        while (true) {
            if (i >= Program.size() && isStationsEmpty(Tomasulo.LoadBuffers, Tomasulo.StoreBuffers, Tomasulo.AdderReservationStations, Tomasulo.MultiplierReservationStations)) {

                    break;

            }
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("Cycle " + iClockCycle + ":" + "\n");


            boolean canIssue = false;
            if (i < Program.size()) {
                Instruction curInstruction = Program.get(i);
                canIssue = IssueInstruction(curInstruction, Tomasulo, i);
            }
            HandleAdderStations(Tomasulo, Executing);
            HandleMultiplierStations(Tomasulo, Executing);
            HandleLoadBuffers(Tomasulo, Executing);
            HandleStoreBuffers(Tomasulo, Executing);

            int writesResult = HandleWriteResult(Tomasulo, Executing);


            Display(Tomasulo, Program, writesResult, canIssue, i, Executing);
            if (canIssue)
                i++;
            iClockCycle++;
        }
    }

    //--------------------------------------------------------------------------------------------------

    private static boolean isStationsEmpty(LoadBuffer[] loadBuffers, StoreBuffer[] storeBuffers, AdderReservationStation[] adderReservationStations, MultiplierReservationStation[] multiplierReservationStations) {
        for (int i = 0; i < loadBuffers.length; i++) {
            if (loadBuffers[i].isBusy())
                return false;
        }
        for (int i = 0; i < storeBuffers.length; i++) {
            if (storeBuffers[i].isBusy())
                return false;
        }
        for (int i = 0; i < adderReservationStations.length; i++) {
            if (adderReservationStations[i].isBusy())
                return false;
        }
        for (int i = 0; i < multiplierReservationStations.length; i++) {
            if (multiplierReservationStations[i].isBusy())
                return false;
        }
        return true;
    }


    //--------------------------------------------------------------------------------------------------

    private static void Display(Main Tomasulo, ArrayList<Instruction> Program, int InstructionIndex, boolean issued, int index, TreeSet<Integer> Executing) {
        System.out.println("Issued instruction: ");
        if (issued) {
            System.out.println(Program.get(index).toString() + "\n");
        } else {
            System.out.println("No empty reservation station to issue instruction \n");
        }

        //--------------------------------------------------------------------------------------------------

        System.out.println("Executing instructions: ");
        for (Integer x : Executing) {
            if (x != -1)
                System.out.println(Program.get(x).toString());
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Writes result: ");
        if (InstructionIndex != -1) {
            System.out.println(Program.get(InstructionIndex).toString() + "\n");
        } else {
            System.out.println("No instruction writes its result\n");
        }

        //--------------------------------------------------------------------------------------------------

        System.out.println("Load Buffers: ");
        System.out.println("   Busy   Address");
        for (int i = 0; i < Tomasulo.LoadBuffers.length; i++) {
            System.out.println(Tomasulo.LoadBuffers[i]);
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Store Buffers: ");
        System.out.println("   Busy   Address  V   Q");
        for (int i = 0; i < Tomasulo.StoreBuffers.length; i++) {
            System.out.println(Tomasulo.StoreBuffers[i]);
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Adder Reservation Stations: ");
        System.out.println("   Busy   Op     Vi     Vk     Qi   Qk  A");
        for (int i = 0; i < Tomasulo.AdderReservationStations.length; i++) {
            System.out.println(Tomasulo.AdderReservationStations[i]);
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Multiplier Reservation Stations: ");
        System.out.println("   Busy   Op     Vi     Vk     Qi    Qk   A");
        for (int i = 0; i < Tomasulo.MultiplierReservationStations.length; i++) {
            System.out.println(Tomasulo.MultiplierReservationStations[i]);
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Cache: ");
        System.out.println(Arrays.toString(Tomasulo.Cache));
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Floating Point Register File: ");
        for (int i = 0; i < Tomasulo.RegisterFile.length; i++) {
            System.out.println(Tomasulo.RegisterFile[i]);
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------
        System.out.println("Integer Register File: ");
        for (int i = 0; i < Tomasulo.IntegerRegisterFile.length; i++) {
            System.out.println(Tomasulo.IntegerRegisterFile[i]);
        }
        System.out.println();
    }


    private static boolean IssueInstruction(Instruction curInstruction, Main Tomasulo, int index) {
        boolean canIssue = false;
        if (curInstruction.getType().equals("ADD.D") || curInstruction.getType().equals("SUB.D")) {
            for (int i = 0; i < Tomasulo.AdderReservationStations.length; i++) {
                AdderReservationStation curStation = Tomasulo.AdderReservationStations[i];
                if (!curStation.isBusy()) {
                    canIssue = true;
                    System.out.println("1");
                    curStation.setBusy(true);
                    curStation.setOpcode(curInstruction.getType());
                    curStation.setInstructionIndex(index);
                    int iRegister1 = curInstruction.getSourceRegister1();
                    int iRegister2 = curInstruction.getSourceRegister2();
                    if (Tomasulo.RegisterFile[iRegister1].getSourceReg().equals("")) {
                        curStation.setSourceValue1(Tomasulo.RegisterFile[iRegister1].getValue());
                    } else {
                        curStation.setSourceReg1(Tomasulo.RegisterFile[iRegister1].getSourceReg());
                    }
                    if (Tomasulo.RegisterFile[iRegister2].getSourceReg().equals("")) {
                        curStation.setSourceValue2(Tomasulo.RegisterFile[iRegister2].getValue());
                    } else {
                        curStation.setSourceReg2(Tomasulo.RegisterFile[iRegister2].getSourceReg());
                    }
                    curStation.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curStation.setStartCycle(iClockCycle);
                    Tomasulo.RegisterFile[curInstruction.getDestinationRegister()].setSourceReg(curStation.getTag());

                    break;
                }
            }
        } else if (curInstruction.getType().equals("MUL.D") || curInstruction.getType().equals("DIV.D")) {
            for (int i = 0; i < Tomasulo.MultiplierReservationStations.length; i++) {
                MultiplierReservationStation curStation = Tomasulo.MultiplierReservationStations[i];
                if (!curStation.isBusy()) {
                    canIssue = true;
                    System.out.println("2");
                    curStation.setBusy(true);
                    curStation.setOpcode(curInstruction.getType());
                    curStation.setInstructionIndex(index);
                    int iRegister1 = curInstruction.getSourceRegister1();
                    int iRegister2 = curInstruction.getSourceRegister2();
                    if (Tomasulo.RegisterFile[iRegister1].getSourceReg().equals("")) {
                        curStation.setSourceValue1(Tomasulo.RegisterFile[iRegister1].getValue());
                    } else {
                        curStation.setSourceReg1(Tomasulo.RegisterFile[iRegister1].getSourceReg());
                    }
                    if (Tomasulo.RegisterFile[iRegister2].getSourceReg().equals("")) {
                        curStation.setSourceValue2(Tomasulo.RegisterFile[iRegister2].getValue());
                    } else {
                        curStation.setSourceReg2(Tomasulo.RegisterFile[iRegister2].getSourceReg());
                    }
                    curStation.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curStation.setStartCycle(iClockCycle);
                    Tomasulo.RegisterFile[curInstruction.getDestinationRegister()].setSourceReg(curStation.getTag());
                    break;
                }
            }
        } else if (curInstruction.getType().equals("L.D")) {
            for (int i = 0; i < Tomasulo.LoadBuffers.length; i++) {
                LoadBuffer curBuffer = Tomasulo.LoadBuffers[i];
                if (!curBuffer.isBusy()) {
                    canIssue = true;
                    System.out.println("3");
                    curBuffer.setBusy(true);
                    curBuffer.setInstructionIndex(index);
                    curBuffer.setAddress(curInstruction.getImmediateValue());
                    curBuffer.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curBuffer.setStartCycle(iClockCycle);
                    Tomasulo.RegisterFile[curInstruction.getDestinationRegister()].setSourceReg(curBuffer.getTag());
                    break;
                }
            }
        } else if (curInstruction.getType().equals("S.D")) {
            for (int i = 0; i < Tomasulo.StoreBuffers.length; i++) {
                StoreBuffer curBuffer = Tomasulo.StoreBuffers[i];
                if (!curBuffer.isBusy()) {
                    canIssue = true;
                    System.out.println("4");
                    curBuffer.setBusy(true);
                    curBuffer.setInstructionIndex(index);
                    curBuffer.setAddress(curInstruction.getImmediateValue());
                    int iRegister1 = curInstruction.getDestinationRegister();
                    System.out.println("iRegister1 " + iRegister1); // expected 4
                    System.out.println(Tomasulo.RegisterFile[iRegister1].getSourceReg()); // expected M1
                    if (Tomasulo.RegisterFile[iRegister1].getSourceReg().equals("")) {
                        curBuffer.setSourceValue(Tomasulo.RegisterFile[iRegister1].getValue());
                    } else {
                        curBuffer.setSourceReg(Tomasulo.RegisterFile[iRegister1].getSourceReg());
                    }
                    curBuffer.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curBuffer.setStartCycle(iClockCycle);
                    break;
                }
            }
        }

        if (curInstruction.getType().equals("ADDI") || curInstruction.getType().equals("SUBI") ) {
            for (int i = 0; i < Tomasulo.AdderReservationStations.length; i++) {
                AdderReservationStation curStation = Tomasulo.AdderReservationStations[i];
                if (!curStation.isBusy()) {
                    canIssue = true;
                    System.out.println("5");
                    curStation.setBusy(true);
                    curStation.setInstructionIndex(index);
                    curStation.setOpcode(curInstruction.getType());
                    int iRegister1 = curInstruction.getSourceRegister1();
                    if (Tomasulo.IntegerRegisterFile[iRegister1].getSourceReg().equals("")) {
                        curStation.setSourceValue1(Tomasulo.IntegerRegisterFile[iRegister1].getValue());
                    } else {
                        curStation.setSourceReg1(Tomasulo.IntegerRegisterFile[iRegister1].getSourceReg());
                    }
                    curStation.setSourceValue2(curInstruction.getImmediateValue());
                    curStation.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curStation.setStartCycle(iClockCycle);
                    Tomasulo.IntegerRegisterFile[curInstruction.getDestinationRegister()].setSourceReg(curStation.getTag());
                    break;
                }
            }
        }

        if (curInstruction.getType().equals("BNEZ")) {
            for (int i = 0; i < Tomasulo.AdderReservationStations.length; i++) {
                AdderReservationStation curStation = Tomasulo.AdderReservationStations[i];
                if (!curStation.isBusy()) {
                    canIssue = true;
                    System.out.println("6");
                    curStation.setBusy(true);
                    curStation.setInstructionIndex(index);
                    curStation.setOpcode(curInstruction.getType());
                    curStation.setLabel(curInstruction.getLabel());
                    int iRegister1 = curInstruction.getDestinationRegister();
                    if (Tomasulo.IntegerRegisterFile[iRegister1].getSourceReg().equals("")) {
                        curStation.setSourceValue1(Tomasulo.IntegerRegisterFile[iRegister1].getValue());
                    } else {
                        curStation.setSourceReg1(Tomasulo.IntegerRegisterFile[iRegister1].getSourceReg());
                    }
//                    curStation.setSourceValue2(curInstruction.getImmediateValue());
                    curStation.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curStation.setStartCycle(iClockCycle);
//                    Tomasulo.IntegerRegisterFile[curInstruction.getDestinationRegister()].setSourceReg(curStation.getTag());
                    break;
                }
            }
        }


        return canIssue;
    }

    private static void HandleAdderStations(Main Tomasulo, TreeSet<Integer> Executing) {
        for (int i = 0; i < Tomasulo.AdderReservationStations.length; i++) {
            AdderReservationStation curStation = Tomasulo.AdderReservationStations[i];
            if (curStation.isBusy() && curStation.getStartCycle() != iClockCycle) {
                if (curStation.getSourceReg1().equals("") && curStation.getSourceReg2().equals("")) {
                    curStation.setTotalCycles(curStation.getTotalCycles() - 1);
                    System.out.println("here adder station " + curStation.getInstructionIndex());
                    Executing.add(curStation.getInstructionIndex());
                    // changed condition to -1, write happens after the last cycle of execution and not
                    // in the same cycle
                    if (curStation.getTotalCycles() == -1) {
                        Tomasulo.WriteResult.add(new String[]{curStation.getTag(), String.valueOf(curStation.getInstructionIndex())});
                        Executing.remove(curStation.getInstructionIndex());

                    }
                }
            }
        }
    }

    private static void HandleMultiplierStations(Main Tomasulo, TreeSet<Integer> Executing) {
        for (int i = 0; i < Tomasulo.MultiplierReservationStations.length; i++) {
            MultiplierReservationStation curStation = Tomasulo.MultiplierReservationStations[i];
            if (curStation.isBusy() && curStation.getStartCycle() != iClockCycle) {
                if (curStation.getSourceReg1().equals("") && curStation.getSourceReg2().equals("")) {
                    System.out.println("here mult station " + curStation.getInstructionIndex());
                    Executing.add(curStation.getInstructionIndex());
                    curStation.setTotalCycles(curStation.getTotalCycles() - 1);
                    if (curStation.getTotalCycles() == -1) {
                        Tomasulo.WriteResult.add(new String[]{curStation.getTag(), String.valueOf(curStation.getInstructionIndex())});
                        Executing.remove(curStation.getInstructionIndex());

                    }
                }
            }
        }
    }

    private static void HandleLoadBuffers(Main Tomasulo, TreeSet<Integer> Executing) {
        for (int i = 0; i < Tomasulo.LoadBuffers.length; i++) {
            LoadBuffer curBuffer = Tomasulo.LoadBuffers[i];
            if (curBuffer.isBusy() && curBuffer.getStartCycle() != iClockCycle) {
                curBuffer.setTotalCycles(curBuffer.getTotalCycles() - 1);
                System.out.println("here load buffer " + curBuffer.getInstructionIndex());
                Executing.add(curBuffer.getInstructionIndex());
                if (curBuffer.getTotalCycles() == -1) {
                    Tomasulo.WriteResult.add(new String[]{curBuffer.getTag(), String.valueOf(curBuffer.getInstructionIndex())});
                    Executing.remove(curBuffer.getInstructionIndex());

                }
            }
        }

    }

    private static void HandleStoreBuffers(Main Tomasulo, TreeSet<Integer> Executing) {
        for (int i = 0; i < Tomasulo.StoreBuffers.length; i++) {
            StoreBuffer curBuffer = Tomasulo.StoreBuffers[i];
            if (curBuffer.isBusy() && curBuffer.getStartCycle() != iClockCycle && curBuffer.getSourceReg().equals("")) {
                curBuffer.setTotalCycles(curBuffer.getTotalCycles() - 1);
                System.out.println("here store buffer " + curBuffer.getInstructionIndex());
                Executing.add(curBuffer.getInstructionIndex());
                if (curBuffer.getTotalCycles() == -1) {
                    Tomasulo.WriteResult.add(new String[]{curBuffer.getTag(), String.valueOf(curBuffer.getInstructionIndex())});
                    Executing.remove(curBuffer.getInstructionIndex());

                }
            }
        }

    }


    private static int HandleWriteResult(Main Tomasulo, TreeSet<Integer> Executing) {
        int iInstructionIndex = -1;
        System.out.println("first time " + !Tomasulo.WriteResult.isEmpty());
        if (!Tomasulo.WriteResult.isEmpty()) {
            String strTag = Tomasulo.WriteResult.poll()[0];
            char cStation = strTag.charAt(0);
            double result = 0;
            System.out.println("initial tag " + strTag.charAt(0));

            switch (cStation) {
                case 'A': {
                    AdderReservationStation curStation = Tomasulo.AdderReservationStations[Integer.parseInt(strTag.substring(1)) - 1];
                    iInstructionIndex = curStation.getInstructionIndex();
                    if (curStation.getOpcode().equals("ADD.D") || curStation.getOpcode().equals("ADDI")) {
                        result = curStation.getSourceValue1() + curStation.getSourceValue2();
                    } else if (curStation.getOpcode().equals("SUB.D") || curStation.getOpcode().equals("SUBI")) {
                        result = curStation.getSourceValue1() - curStation.getSourceValue2();
                    }
                    else if (curStation.getOpcode().equals("BNEZ")) {
                        if (curStation.getSourceValue1() != 0) {
                            i = hmLabels.get(curStation.getLabel());

                        }
                    }
                    curStation.setBusy(false);
                    curStation.setSourceValue1(0);
                    curStation.setSourceValue2(0);
                    curStation.setSourceReg1("");
                    curStation.setSourceReg2("");
                    curStation.setInstructionIndex(-1);
                }
                break;
                case 'M': {
                    System.out.println("entered");
                    MultiplierReservationStation curStation = Tomasulo.MultiplierReservationStations[Integer.parseInt(strTag.substring(1)) - 1];
                    iInstructionIndex = curStation.getInstructionIndex();
                    if (curStation.getOpcode().equals("MUL.D")) {
                        result = curStation.getSourceValue1() * curStation.getSourceValue2();
                    } else if (curStation.getOpcode().equals("DIV.D")) {
                        result = curStation.getSourceValue1() / curStation.getSourceValue2();
                    }
                    curStation.setBusy(false);
                    curStation.setSourceValue1(0);
                    curStation.setSourceValue2(0);
                    curStation.setSourceReg1("");
                    curStation.setSourceReg2("");
                    curStation.setInstructionIndex(-1);
                }
                break;
                case 'L': {
                    LoadBuffer curBuffer = Tomasulo.LoadBuffers[Integer.parseInt(strTag.substring(1)) - 1];
                    iInstructionIndex = curBuffer.getInstructionIndex();
                    result = Tomasulo.Cache[curBuffer.getAddress()];
                    curBuffer.setBusy(false);
                    curBuffer.setAddress(0);
                    curBuffer.setInstructionIndex(-1);

                }
                break;
                default: //case 'S'
                {
                    StoreBuffer curBuffer = Tomasulo.StoreBuffers[Integer.parseInt(strTag.substring(1)) - 1];
                    iInstructionIndex = curBuffer.getInstructionIndex();
                    result = curBuffer.getSourceValue();
                    Tomasulo.Cache[curBuffer.getAddress()] = result;
                    curBuffer.setBusy(false);
                    curBuffer.setAddress(0);
                    curBuffer.setSourceValue(0);
                    curBuffer.setInstructionIndex(-1);

                }
            }

            for (int j = 0; j < Tomasulo.RegisterFile.length; j++) {
                if (Tomasulo.RegisterFile[j].getSourceReg().equals(strTag)) {
                    Tomasulo.RegisterFile[j].setSourceReg("");
                    Tomasulo.RegisterFile[j].setValue(result);
                }
            }
            for (int j = 0; j < Tomasulo.IntegerRegisterFile.length; j++) {
                if (Tomasulo.IntegerRegisterFile[j].getSourceReg().equals(strTag)) {
                    Tomasulo.IntegerRegisterFile[j].setSourceReg("");
                    Tomasulo.IntegerRegisterFile[j].setValue((int)result);
                }
            }

            for (int j = 0; j < Tomasulo.AdderReservationStations.length; j++) {
                if (Tomasulo.AdderReservationStations[j].getSourceReg1().equals(strTag)) {
                    Tomasulo.AdderReservationStations[j].setSourceValue1(result);
                    Tomasulo.AdderReservationStations[j].setSourceReg1("");
                }
                if (Tomasulo.AdderReservationStations[j].getSourceReg2().equals(strTag)) {
                    Tomasulo.AdderReservationStations[j].setSourceValue2(result);
                    Tomasulo.AdderReservationStations[j].setSourceReg2("");
                }
            }
            for (int j = 0; j < Tomasulo.MultiplierReservationStations.length; j++) {
                if (Tomasulo.MultiplierReservationStations[j].getSourceReg1().equals(strTag)) {
                    Tomasulo.MultiplierReservationStations[j].setSourceValue1(result);
                    Tomasulo.MultiplierReservationStations[j].setSourceReg1("");
                }
                if (Tomasulo.MultiplierReservationStations[j].getSourceReg2().equals(strTag)) {
                    Tomasulo.MultiplierReservationStations[j].setSourceValue2(result);
                    Tomasulo.MultiplierReservationStations[j].setSourceReg2("");
                }
            }
            for (int j = 0; j < Tomasulo.StoreBuffers.length; j++) {
                if (Tomasulo.StoreBuffers[j].getSourceReg().equals(strTag)) {
                    Tomasulo.StoreBuffers[j].setSourceValue(result);
                    Tomasulo.StoreBuffers[j].setSourceReg("");
                }
            }

            if (Executing.contains(iInstructionIndex))
                Executing.remove(iInstructionIndex);

        }

        return iInstructionIndex;
    }

}
