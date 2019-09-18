package final_project.classes;

import java.util.ArrayList;

/**
 *
 * @author Deep Shah
 */
public class ShiftList {

    private ArrayList<Shift> shiftList = new ArrayList();

    /**
     * No parameter constructor
     */
    public ShiftList() {
    }

    /**
     * Add shift to the arrayList
     *
     * @param shift
     */
    public void add(Shift shift) {
        shiftList.add(shift);
    }

    /**
     * delete the shift feom the arraylist
     *
     * @param shift
     */
    public void delete(Shift shift) {
        shiftList.remove(shift);
    }

    /**
     * gets the size of the arraylist
     *
     * @return
     */
    public int size() {
        return shiftList.size();
    }

    /**
     * gets the specific shift from the arraylist
     *
     * @param Index
     * @return shift
     */
    public Shift get(int Index) {

        if (Index <= shiftList.size()) {
            return shiftList.get(Index);
        } else {
            return null;
        }
    }
}
