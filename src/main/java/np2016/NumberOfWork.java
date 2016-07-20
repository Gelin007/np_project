package np2016;

import java.util.ArrayList;

public class NumberOfWork {

private ArrayList<Integer> numberOfWork;

public NumberOfWork(int numberOfThreads){
	numberOfWork=new ArrayList<Integer>();
	for (int i = 0; i < numberOfThreads; i++) {
		numberOfWork.add(i, 0);
}
}

synchronized public void set (int pos, int work){
numberOfWork.set(pos, work);	

}

synchronized public void increase(int pos){
	numberOfWork.set(pos, numberOfWork.get(pos) + 1);
	
}

synchronized public void decrease(int pos){
	numberOfWork.set(pos, numberOfWork.get(pos) - 1);
	
}

synchronized public boolean isWorkFinished (){
	boolean check = true;
	for (int i = 0; i < numberOfWork.size(); i++) {
		if (numberOfWork.get(i) != 0)
			check = false;
	}
	return check;
}

}
