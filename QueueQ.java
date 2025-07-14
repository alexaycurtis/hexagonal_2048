import java.util.*;
class QueueQ{
    private int[] array;
    private int front;
    private int end;
    public int size;

    public QueueQ(int arraySize){//constructor
        array = new int[arraySize];
        front = 0;
        end = -1;
        size = 0;
    }
    public boolean isEmpty(){//check if queue contains elements or not
        return size==0;
    }

    public boolean isFull(){//check if queue is filled all the way
        return size==array.length;
    }

    public void enqueue(int element){//adds item from rear
        if(isFull()){
            System.out.println("Error. Queue is full.");
            return;
        }
        
        if(end == array.length-1){
            end = 0; //restart increment bc its circular and end value needs to wrap around queue
        }
        else{
            end++;
        }

        array[end] = element;
        size++;

    }

    public int dequeue(){//removes item from front
        if(isEmpty()){
            System.out.println("Queue is empty. No element can be dequeued.");
            return -1;
        }
        else{
            int removedItem = array[front];
            array[front] = -1;
            if(front == array.length-1){
                front = 0; //restart increment bc its circular
            }
            else{
                front++;
            }
            size--;//decrease theoretical size
            return removedItem;
        }

    }

    public int peek(){//look at first element in the queue
        if(isEmpty()){
            return -1;
        }
        return array[front];
    }
    
    public void shuffle(){//uses fisher-yates shuffle to randomly swap elements in the array
      if(isEmpty()){
        return;
      }
      
      int[] tempArray = new int[size];//temp array to store all elements
      int originalSize = size;
      
      //Dequeue elements into temporary array
      for(int i = 0; i <originalSize; i++){
          tempArray[i] = dequeue();
      }
      
      //shuffle the temporary array using Fisher-Yates :(
      Random rand = new Random();
      for(int i = tempArray.length - 1; i>0; i--){
          int j = rand.nextInt(i + 1);
          //swap i and j
          int temp = tempArray[i];
          tempArray[i] = tempArray[j];
          tempArray[j] = temp;
      }
      
      //enqueue elements back
      for(int i = 0; i <tempArray.length; i++){
          enqueue(tempArray[i]);
      }
    }
}
