
import java.util.*;
class Stack<E>{
    private E[] array;
    private int front;
    private int size;

    Stack(int arraySize){//constructor
        array = (E[]) new Object[arraySize];
        front = -1;
        size = 0;
    }
    

    public E pop(){//remove most recently added item in stack
        if(isEmpty()){
            System.out.println("Stack is empty. No element can be popped.");
            return null;
        }

        E removedItem = array[front];
        array[front] = null;
            
        front--;
        size--;
        return removedItem;//copy paste from deque then simplified

    }
    
    public E popLast(){//remove oldest item added in stack
      if(isEmpty()){
        System.out.println("Stack is empty. No element can be popped.");
        return null;
      }
      
      E removedItem = array[0];
      
      for(int i =0; i<front;i++){
        array[i] = array[i+1];
      }
      
      array[front] = null;
      front--;
      size--;
      
      return removedItem;
    }

    public void push(E element){//add item to front of stack
        if(isFull()){
            System.out.println("Error. Stack is full.");
            return;
        }
        
        array[++front] = element;//increment the front before to get the right position on the top of the stack
        size++;
        //even easier pop basically but like adding element instead of removing
    }

    public E peek(){//look at first element in the stack
        if(isEmpty()){
            return null;
        }
        return array[front];
    }

    public boolean isEmpty(){//check if stack contains elements or not
        return size==0;
    }

    public boolean isFull(){//check if stack is filled all the way
        return size==array.length;
    }

}
