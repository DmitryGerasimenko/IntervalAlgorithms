import com.kenai.jinterval.fast_nearest_round.DoubleInterval;
import java.util.Comparator;
import java.util.PriorityQueue;

public class IAOGO {
    PriorityQueue<ListElem> wList = new PriorityQueue<ListElem>(new AssessmentComp()); //is used for storage objects of ListElem

    private DoubleInterval c_1, c_2;
    
    private void initConstants() {
        c_1 = DoubleInterval.valueOf(1,1);
        c_2 = DoubleInterval.valueOf(2,2);
    }
    
    public DoubleInterval objectiveFunction(DoubleInterval[] origin) { // origin = [a b theta]
        // returning value is a^2 + (b+1)^2 - 2*a * (b+1) * sin(theta)
        //if you want to find max of function - just use .neg() to whole expression, and take off minus from result
        DoubleInterval a = origin[0];
        DoubleInterval b = origin[1];
        DoubleInterval theta = origin[2];
        return a.sqr()
                .add(b.add(c_1).sqr())
                .subtract(c_2.multiply(a).multiply(b.add(c_1)).multiply(theta.sin()));
    }

    public void IntFunc() {
        DoubleInterval[] first = wList.peek().getData().clone();
        DoubleInterval[] second = first.clone();
        //we initialize two clones of box, which stores in leading element of the list
        //next step - find the widest component of box
        int j = 0;
        double max = first[0].width();
        for (int i = 1; i < first.length; i++)
            if (first[i].width() > max) {
                max = first[i].width();
                j = i;
            }
        //now we bisect each clone by the widest component, first - from lower boundary to middle, second - from middle to upper boundary
        first[j] = DoubleInterval.valueOf(first[j].doubleInf(),first[j].doubleMid());
        second[j] = DoubleInterval.valueOf(second[j].doubleMid(),second[j].doubleSup());
        //here we create two elements of list with corresponding ranges of objective function and mins
        ListElem firstel = new ListElem(first, objectiveFunction(first).doubleInf());
        ListElem secondel = new ListElem(second, objectiveFunction(second).doubleInf());
        //further - remove leading element
        wList.poll();
        //and add new two with automatic collation, provided by PriorityQueue collection
        wList.add(firstel);
        wList.add(secondel);
    }

    public void start(DoubleInterval[] box, double tolerance) {
        initConstants();
        DoubleInterval intArea = objectiveFunction(box); //now we have defined range of objective function
        double intwid = intArea.doubleWid(); //width of range will be used for stopping criterion
        ListElem wrk = new ListElem(box,intArea.doubleInf());
        //at this step we initialized new element of Our List, it's consist of domain of objective function and the min of range of objective function
        wList.add(wrk);
        // when new element adds to the list, it's automatically compare with other elements of list by second field (min), and sorts in ascending order
        while (intwid >= tolerance) {
            IntFunc(); // check IntFunc() for detailed description
            intwid = objectiveFunction(wList.peek().getData()).doubleWid(); //here we take the min of leading element of the List
        }
    }
}

class AssessmentComp implements Comparator<ListElem> {
    public int compare(ListElem el1, ListElem el2) {
        return Double.valueOf(el1.getAssessment()).compareTo(el2.getAssessment());
    }
}

class ListElem implements Comparable<ListElem> {

    private DoubleInterval[] data; //here we store domain of objective function
    private double assessment; //this field represents infumum of range of objective function for ListElem

    ListElem() {
        data = null;//new DoubleInterval[];
        assessment = 0;
    }

    ListElem(DoubleInterval[] data, double assessment) {
        this.data = data;
        this.assessment = assessment;
    }

    public double getAssessment() {
        return assessment;
    }
    public void setAssessment(double assessment) {
        this.assessment = assessment;
    }
    public DoubleInterval[] getData() {
        return this.data;
    }
    public void setData(DoubleInterval[] data) {
        this.data = data;
    }
    public int compareTo(ListElem el) { //at this place and at strings 55-59 we redefined comparator for comparison ListElem objects by "assessment" field
        return Double.valueOf(this.assessment).compareTo(Double.valueOf(el.assessment));
    }
}
