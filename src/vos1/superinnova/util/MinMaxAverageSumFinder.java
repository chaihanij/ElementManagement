/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.util;

/**
 *
 * @author HugeScreen
 */
public class MinMaxAverageSumFinder {
    
    int members=0;
    double min=Double.MAX_VALUE;
    double max=Double.MIN_VALUE;
    double sum=0;

    public int getMembers() {
        return members;
    }

    public double getMin() {
        if(this.sum==0){
            return 0;
        }
        return min;
    }

    public double getMax() {
        if(this.sum==0){
            return 0;
        }        
        return max;
    }

    public double getSum() {
        return sum;
    }
    public double getAverage(){
        if(members>0){
            return this.sum/this.members;
        }
        else{
            return 0d;
        }
    }
    
    
    
    public void addMember(double value){
        if(value < min){
            min = value;
        }
        if(value > max){
            max = value;
        }
        sum+=value;
        members++;
    }
    
    public void dump(){
        System.out.println("==========================");
        System.out.println("Members : "+this.members);
        System.out.println("Min : "+this.min);
        System.out.println("Max : "+this.max);
        System.out.println("Sum : "+this.sum);
        System.out.println("Average : "+this.getAverage());
        
    }
    
    
    public static void main(String[] args) {
        MinMaxAverageSumFinder minMaxAverageSumFinder = new MinMaxAverageSumFinder();
        minMaxAverageSumFinder.dump();
        minMaxAverageSumFinder.addMember(3d);
        minMaxAverageSumFinder.dump();
        minMaxAverageSumFinder.addMember(3d);
        minMaxAverageSumFinder.dump();
        minMaxAverageSumFinder.addMember(1d);
        minMaxAverageSumFinder.dump();
    }
}
