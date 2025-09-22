package com.example;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DivideConquerAlgorithmsTest {

    @Test
    public void testInsertionSort() {
        DivideConquerAlgorithms algo = new DivideConquerAlgorithms();
        int[] array = {12, 3, 7, 9, 14, 6, 11, 2};
        algo.insertionSort(array, array.length);
        Assertions.assertArrayEquals(new int[]{2, 3, 6, 7, 9, 11, 12, 14}, array);
    }

    @Test
    public void testMergeSort() {
        DivideConquerAlgorithms algo = new DivideConquerAlgorithms();
        int[] array = {12, 3, 7, 9, 14, 6, 11, 2};
        algo.mergeSort(array, 0, array.length - 1);
        Assertions.assertArrayEquals(new int[]{2, 3, 6, 7, 9, 11, 12, 14}, array);
    }

    @Test
    public void testBinarySearch() {
        DivideConquerAlgorithms algo = new DivideConquerAlgorithms();
        int[] array = {2, 3, 6, 7, 9, 11, 12, 14};
        int result1 = algo.binarySearch(array, 0, array.length - 1, 11);
        Assertions.assertEquals(5, result1);
        int result2 = algo.binarySearch(array, 0, array.length - 1, 5);
        Assertions.assertEquals(-1, result2);
    }

    @Test
    public void testFindMax() {
        DivideConquerAlgorithms algo = new DivideConquerAlgorithms();
        int[] array = {2, 3, 6, 7, 9, 11, 12, 14};
        int result = algo.findMax(array, 0, array.length - 1);
        Assertions.assertEquals(14, result);
    }
}