def insertion_sort(arr):
    for i in range(1, len(arr)):
        key = arr[i]
        j = i - 1
        while j >= 0 and arr[j] > key:
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = key
    return arr

def merge_sort(arr):
    if len(arr) > 1:
        mid = len(arr) // 2
        left_half = arr[:mid]
        right_half = arr[mid:]
        
        merge_sort(left_half)
        merge_sort(right_half)
        
        i = j = k = 0
        
        while i < len(left_half) and j < len(right_half):
            if left_half[i] < right_half[j]:
                arr[k] = left_half[i]
                i += 1
            else:
                arr[k] = right_half[j]
                j += 1
            k += 1
        
        while i < len(left_half):
            arr[k] = left_half[i]
            i += 1
            k += 1
        
        while j < len(right_half):
            arr[k] = right_half[j]
            j += 1
            k += 1
    return arr

def binary_search(arr, left, right, target):
    if left <= right:
        mid = left + (right - left) // 2
        
        if arr[mid] == target:
            return mid
        
        if arr[mid] > target:
            return binary_search(arr, left, mid - 1, target)
        
        return binary_search(arr, mid + 1, right, target)
    
    return -1
def find_max(arr, left, right):
    if left == right:
        return arr[left]
    mid = left + (right - left) // 2
    max_left = find_max(arr, left, mid)
    max_right = find_max(arr, mid + 1, right)
    return max(max_left, max_right)
def test_algorithms():
    test_data = [12, 3, 7, 9, 14, 6, 11, 2]
    print(f"data: {test_data}")

    insertion_data = test_data.copy()
    insertion_sort(insertion_data)
    print(f"Insertion Sort: {insertion_data}")
    assert insertion_data == [2, 3, 6, 7, 9, 11, 12, 14], "1st"
    
    merge_data = test_data.copy()
    merge_sort(merge_data)
    print(f"Merge Sort: {merge_data}")
    assert merge_data == [2, 3, 6, 7, 9, 11, 12, 14], "1st"
    
    target = 11
    index = binary_search(merge_data, 0, len(merge_data) - 1, target)
    print(f"Binary Search for {target}: {'Found at index ' + str(index) if index != -1 else 'Not found'}")
    assert index == 5, "Binary Search failed"
    
    max_value = find_max(test_data, 0, len(test_data) - 1)
    print(f"Maximum value: {max_value}")
    assert max_value == 14, "1st"

if __name__ == "__main__":
    test_algorithms()