package com.common.toolkit.datastructure;

/**
 * 排序
 *
 * @author ewen
 */
public class Sorts {

  /**
   * 插入排序
   *
   * @param a 表示数组
   * @param n 表示数组大小
   */
  public static void insertionSort(int[] a, int n) {
    if (n <= 1) {
      return;
    }

    for (int i = 1; i < n; ++i) {
      int value = a[i];
      int j = i - 1;
      // 查找插入的位置
      for (; j >= 0; --j) {
        if (a[j] > value) {
          // 数据移动
          a[j + 1] = a[j];
        } else {
          break;
        }
      }
      // 插入数据
      a[j + 1] = value;
    }
  }

  /**
   * 冒泡排序
   *
   * @param a 表示数组
   * @param n 表示数组大小
   */
  public static void bubbleSort(int[] a, int n) {
    if (n <= 1) {
      return;
    }

    for (int i = 0; i < n; ++i) {
      // 提前退出冒泡循环的标志位
      boolean flag = false;
      for (int j = 0; j < n - i - 1; ++j) {
        // 交换
        if (a[j] > a[j + 1]) {
          int tmp = a[j];
          a[j] = a[j + 1];
          a[j + 1] = tmp;
          // 表示有数据交换
          flag = true;
        }
      }
      // 没有数据交换，提前退出
      if (!flag) {
        break;
      }
    }
  }

  /**
   * 选择排序
   *
   * @param a 表示数组
   * @param n 表示数组大小
   */
  public static void selectionSort(int[] a, int n) {
    if (n <= 1) {
      return;
    }
    for (int i = 0; i < n - 1; ++i) {
      // 查找最小值
      int minIndex = i;
      for (int j = i + 1; j < n; ++j) {
        if (a[j] < a[minIndex]) {
          minIndex = j;
        }
      }

      // 交换
      int tmp = a[i];
      a[i] = a[minIndex];
      a[minIndex] = tmp;
    }
  }

  /**
   * 向下冒泡。可能比冒泡更易懂？
   *
   * 算法概要： 从0开始，用这个元素去跟后面的所有元素比较，如果发现这个元素大于后面的某个元素，则交换。 3 2 6 4 5 1 第一趟是从 index=0 也就是 3，
   * 开始跟index=1及其后面的数字比较 3 大于 2，交换，变为 2 3 6 4 5 1，此时index=0的位置变为了2 接下来将用2跟index=2比较 2 不大于 6 不交换 2
   * 不大于 4 不交换 2 不大于 5 不交换 2 大于 1，交换，变为 1 3 6 4 5 2，第一趟排序完成。
   *
   * 第二趟是从 index=1 也就是 3，开始跟index=2及其后面的数字比较 3 不大于 6 不交换 3 不大于 4 不交换 3 不大于 5 不交换 3 大于 2，交换，变为 1 2 6
   * 4 5 3，第二趟排序完成。
   *
   * 第三趟是从 index=2 也就是 6，开始跟index=3及其后面的数字比较 6 大于 4，交换，变为 1 2 4 6 5 3, 此时 index = 2 的位置变为了4
   * 接下来将用4跟index=4比较 4 不大于 5 不交换 4 大于 3，交换，变为 1 2 3 6 5 4，第三趟排序完成。
   *
   * 第四趟是从 index=3 也就是 6，开始跟index=4及其后面的数字比较 6 大于 5，交换，变为 1 2 3 5 6 4, 此时 index = 3 的位置变为了5
   * 接下来将用5跟index=5比较 5 大于 4，交换，变为 1 2 3 4 6 5, 第四趟排序完成。
   *
   * 第五趟是从 index=4 也就是 6，开始跟index=5及其后面的数字比较 6 大于 5，交换，变为 1 2 3 4 5 6, 此时 index = 4 的位置变为了5
   * 接下来将用5跟index=6比较 index = 6 已经不满足 index < length 的条件，整个排序完成。
   */
  private static void bubbleDownSort(int[] arr) {
    int len = arr.length;
    if (len == 1) {
      return;
    }

    for (int i = 0; i < len; i++) {
      for (int j = i + 1; j < len; j++) {
        if (arr[i] > arr[j]) {
          int tmp = arr[i];
          arr[i] = arr[j];
          arr[j] = tmp;
        }
      }
    }
  }

  /**
   * 希尔排序
   */
  private static void shellSort(int[] arr) {
    int len = arr.length;
    if (len == 1) {
      return;
    }

    int step = len / 2;
    while (step >= 1) {
      for (int i = step; i < len; i++) {
        int value = arr[i];
        int j = i - step;
        for (; j >= 0; j -= step) {
          if (value < arr[j]) {
            arr[j + step] = arr[j];
          } else {
            break;
          }
        }
        arr[j + step] = value;
      }
      step = step / 2;
    }
  }

  /**
   * 归并排序算法
   *
   * @param a 是数组
   * @param n 表示数组大小
   */
  public static void mergeSort(int[] a, int n) {
    mergeSortInternally(a, 0, n - 1);
  }

  // 递归调用函数
  private static void mergeSortInternally(int[] a, int p, int r) {
    // 递归终止条件
    if (p >= r) {
      return;
    }

    // 取p到r之间的中间位置q
    int q = (p + r) / 2;
    // 分治递归
    mergeSortInternally(a, p, q);
    mergeSortInternally(a, q + 1, r);

    // 将A[p...q]和A[q+1...r]合并为A[p...r]
    merge(a, p, q, r);
  }

  private static void merge(int[] a, int p, int q, int r) {
    int i = p;
    int j = q + 1;
    // 初始化变量i, j, k
    int k = 0;
    // 申请一个大小跟a[p...r]一样的临时数组
    int[] tmp = new int[r - p + 1];
    while (i <= q && j <= r) {
      if (a[i] <= a[j]) {
        tmp[k++] = a[i++]; // i++等于i:=i+1
      } else {
        tmp[k++] = a[j++];
      }
    }
    // 判断哪个子数组中有剩余的数据
    int start = i;
    int end = q;
    if (j <= r) {
      start = j;
      end = r;
    }
    // 将剩余的数据拷贝到临时数组tmp
    while (start <= end) {
      tmp[k++] = a[start++];
    }
    // 将tmp中的数组拷贝回a[p...r]
    for (i = 0; i <= r - p; ++i) {
      a[p + i] = tmp[i];
    }
  }

  /**
   * 快速排序
   *
   * @param a 是数组
   * @param n 表示数组的大小
   */
  public static void quickSort(int[] a, int n) {
    quickSortInternally(a, 0, n - 1);
  }

  // 快速排序递归函数，p,r为下标
  private static void quickSortInternally(int[] a, int p, int r) {
    if (p >= r) {
      return;
    }
    int q = partition(a, p, r); // 获取分区点
    quickSortInternally(a, p, q - 1);
    quickSortInternally(a, q + 1, r);
  }

  private static int partition(int[] a, int p, int r) {
    int pivot = a[r];
    int i = p;
    for (int j = p; j < r; ++j) {
      if (a[j] < pivot) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        ++i;
      }
    }
    int tmp = a[i];
    a[i] = a[r];
    a[r] = tmp;
    return i;
  }

  /**
   * 计数排序，假设数组中存储的都是非负整数。
   *
   * @param a 是数组
   * @param n 是数组大小
   */
  public static void countingSort(int[] a, int n) {
    if (n <= 1) {
      return;
    }

    // 查找数组中数据的范围
    int max = a[0];
    for (int i = 1; i < n; ++i) {
      if (max < a[i]) {
        max = a[i];
      }
    }

    // 申请一个计数数组c，下标大小[0,max]
    int[] c = new int[max + 1];
    for (int i = 0; i < max + 1; ++i) {
      c[i] = 0;
    }

    // 计算每个元素的个数，放入c中
    for (int i = 0; i < n; ++i) {
      c[a[i]]++;
    }

    // 依次累加
    for (int i = 1; i < max + 1; ++i) {
      c[i] = c[i - 1] + c[i];
    }

    // 临时数组r，存储排序之后的结果
    int[] r = new int[n];
    // 计算排序的关键步骤了，有点难理解
    for (int i = n - 1; i >= 0; --i) {
      int index = c[a[i]] - 1;
      r[index] = a[i];
      c[a[i]]--;
    }

    // 将结果拷贝会a数组
    for (int i = 0; i < n; ++i) {
      a[i] = r[i];
    }
  }

}
