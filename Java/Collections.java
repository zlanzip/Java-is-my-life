package Java.util;
 
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
 
public class Collections {
	// Suppresses default constructor, ensuring non-instantiability.
	private Collections() {
	}
 
	// 算法
 
	/*
	 * 
	 * 算法需要用到的一些参数。所有的关于List的算法都有两种实现，一种是适合随机访问的 List，另一种是适合连续访问的。
	 */
	private static final int BINARYSEARCH_THRESHOLD = 5000;
	private static final int REVERSE_THRESHOLD = 18;
	private static final int SHUFFLE_THRESHOLD = 5;
	private static final int FILL_THRESHOLD = 25;
	private static final int ROTATE_THRESHOLD = 100;
	private static final int COPY_THRESHOLD = 10;
	private static final int REPLACEALL_THRESHOLD = 11;
	private static final int INDEXOFSUBLIST_THRESHOLD = 35;
 
	/**
	 *
	 * List中的所有元素必须实现Compareable接口,即每个 元素必须是可比的。
	 * 
	 * 算法的实现原理为： 把指定的List转化为一个对象数组，对数组进行排序，然后迭代List的每一个元素， 在同样的位置重新设置数组中排好序的元素
	 */
	public static <T extends Comparable<? super T>> void sort(List<T> list) {
		Object[] a = list.toArray();
		// 转化为对象数组
		Arrays.sort(a);
		// 对数组排序，使用了归并排序.对此归并的详细分析可见我另一篇博客
		ListIterator<T> i = list.listIterator();
		for (int j = 0; j < a.length; j++) {
			// 迭代元素
			i.next();
			i.set((T) a[j]);
			// 在同样的位置重设排好序的值
		}
	}
 
	/**
	 * 传一个实现了Comparator接口的对象进来。 c.compare(o1,o2);来比较两个元素
	 */
	public static <T> void sort(List<T> list, Comparator<? super T> c) {
		Object[] a = list.toArray();
		Arrays.sort(a, (Comparator) c);
		ListIterator i = list.listIterator();
		for (int j = 0; j < a.length; j++) {
			i.next();
			i.set(a[j]);
		}
	}
 
	/**
	 *
	 * 使用二分查找在指定List中查找指定元素key。 List中的元素必须是有序的。如果List中有多个key，不能确保哪个key值被找到。
	 * 如果List不是有序的，返回的值没有任何意义
	 * 
	 * 对于随机访问列表来说，时间复杂度为O(log(n)),比如1024个数只需要查找log2(1024)=10次，
	 * log2(n)是最坏的情况，即最坏的情况下都只需要找10次
	 * 对于链表来说，查找中间元素的时间复杂度为O(n),元素比较的时间复杂度为O(log(n))
	 * 
	 * @return 查找元素的索引。如果返回的是负数表明找不到此元素，但可以用返回值计算
	 *         应该将key插入到集合什么位置，任然能使集合有序(如果需要插入key值的话)。 公式：point = -i - 1
	 * 
	 */
	public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) {
		if (list instanceof RandomAccess || list.size() < BINARYSEARCH_THRESHOLD)
			return Collections.indexedBinarySearch(list, key);
		else
			return Collections.iteratorBinarySearch(list, key);
	}
 
	/**
	 * 使用索引化二分查找。 size小于5000的链表也用此方法查找
	 */
	private static <T> int indexedBinarySearch(List<? extends Comparable<? super T>> list, T key) {
		int low = 0; // 元素所在范围的下界
		int high = list.size() - 1;
		// 上界
 
		while (low <= high) {
			int mid = (low + high) >>> 1;
			Comparable<? super T> midVal = list.get(mid);
			// 中间值
			int cmp = midVal.compareTo(key);
			// 指定元素与中间值比较
 
			if (cmp < 0)
				low = mid + 1;
			// 重新设置上界和下界
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found
	}
 
	/**
	 * 迭代式二分查找，线性查找，依次查找得中间值
	 * 
	 */
	private static <T> int iteratorBinarySearch(List<? extends Comparable<? super T>> list, T key) {
		int low = 0;
		int high = list.size() - 1;
		ListIterator<? extends Comparable<? super T>> i = list.listIterator();
 
		while (low <= high) {
			int mid = (low + high) >>> 1;
			Comparable<? super T> midVal = get(i, mid);
			int cmp = midVal.compareTo(key);
 
			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found
	}
 
	private static <T> T get(ListIterator<? extends T> i, int index) {
		T obj = null;
		int pos = i.nextIndex(); // 根据当前迭代器的位置确定是向前还是向后遍历找中间值
		if (pos <= index) {
			do {
				obj = i.next();
			} while (pos++ < index);
		} else {
			do {
				obj = i.previous();
			} while (--pos > index);
		}
		return obj;
	}
 
	/**
	 * 提供实现了Comparator接口的对象比较元素
	 */
	public static <T> int binarySearch(List<? extends T> list, T key, Comparator<? super T> c) {
		if (c == null)
			return binarySearch((List) list, key);
 
		if (list instanceof RandomAccess || list.size() < BINARYSEARCH_THRESHOLD)
			return Collections.indexedBinarySearch(list, key, c);
		else
			return Collections.iteratorBinarySearch(list, key, c);
	}
 
	private static <T> int indexedBinarySearch(List<? extends T> l, T key, Comparator<? super T> c) {
		int low = 0;
		int high = l.size() - 1;
 
		while (low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = l.get(mid);
			int cmp = c.compare(midVal, key);
 
			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found
	}
 
	private static <T> int iteratorBinarySearch(List<? extends T> l, T key, Comparator<? super T> c) {
		int low = 0;
		int high = l.size() - 1;
		ListIterator<? extends T> i = l.listIterator();
 
		while (low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = get(i, mid);
			int cmp = c.compare(midVal, key);
 
			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found
	}
 
	private interface SelfComparable extends Comparable<SelfComparable> {
	}
 
	/**
	 * 
	 * 逆序排列指定列表中的元素
	 */
	public static void reverse(List<?> list) {
		int size = list.size();
		// 如果是size小于18的链表或是基于随机访问的列表
		if (size < REVERSE_THRESHOLD || list instanceof RandomAccess) {
			for (int i = 0, mid = size >> 1, j = size - 1; i < mid; i++, j--)
				// 第一个与最后一个，依次交换
				swap(list, i, j); // 交换i和j位置的值
		} else { // 基于迭代器的逆序排列算法
			ListIterator fwd = list.listIterator();
			ListIterator rev = list.listIterator(size);
			for (int i = 0, mid = list.size() >> 1; i < mid; i++) {
				// 这..，一个思想你懂得
				Object tmp = fwd.next();
				fwd.set(rev.previous());
				rev.set(tmp);
			}
		}
	}
 
	/**
	 * 
	 * 对指定列表中的元素进行混排
	 */
	public static void shuffle(List<?> list) {
		if (r == null) {
			r = new Random();
		}
		shuffle(list, r);
	}
 
	private static Random r;
 
	/**
	 * 
	 * 提供一个随机数生成器对指定List进行混排
	 * 
	 * 基本算法思想为： 逆向遍历list，从最后一个元素到第二个元素，然后重复交换当前位置 与随机产生的位置的元素值。
	 *
	 * 如果list不是基于随机访问并且其size>5,会先把List中的复制到数组中， 然后对数组进行混排，再把数组中的元素重新填入List中。
	 * 这样做为了避免迭代器大跨度查找元素影响效率
	 */
	public static void shuffle(List<?> list, Random rnd) {
		int size = list.size();
		if (size < SHUFFLE_THRESHOLD || list instanceof RandomAccess) {
			for (int i = size; i > 1; i--) // 从i-1个位置开始与随机位置元素交换值
				swap(list, i - 1, rnd.nextInt(i));
		} else {
			Object arr[] = list.toArray(); // 先转化为数组
 
			// 对数组进行混排
			for (int i = size; i > 1; i--)
				swap(arr, i - 1, rnd.nextInt(i));
 
			// 然后把数组中的元素重新填入List
			ListIterator it = list.listIterator();
			for (int i = 0; i < arr.length; i++) {
				it.next();
				it.set(arr[i]);
			}
		}
	}
 
	/**
	 * 交换List中两个位置的值
	 */
	public static void swap(List<?> list, int i, int j) {
		final List l = list;
		l.set(i, l.set(j, l.get(i)));
		// 互换i和j位置的值
	}
 
	/**
	 * 交换数组俩位置的值。好熟悉啊
	 */
	private static void swap(Object[] arr, int i, int j) {
		Object tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}
 
	/**
	 * 
	 * 用obj替换List中的所有元素 依次遍历赋值即可
	 */
	public static <T> void fill(List<? super T> list, T obj) {
		int size = list.size();
 
		if (size < FILL_THRESHOLD || list instanceof RandomAccess) {
			for (int i = 0; i < size; i++)
				list.set(i, obj);
		} else {
			ListIterator<? super T> itr = list.listIterator();
			for (int i = 0; i < size; i++) {
				itr.next();
				itr.set(obj);
			}
		}
	}
 
	/**
	 * 
	 * 复制源列表的所有元素到目标列表， 如果src.size > dest.size 将抛出一个异常 如果src.size < dest.size
	 * dest中多出的元素将不受影响 同样是依次遍历赋值
	 */
	public static <T> void copy(List<? super T> dest, List<? extends T> src) {
		int srcSize = src.size();
		if (srcSize > dest.size())
			throw new IndexOutOfBoundsException("Source does not fit in dest");
 
		if (srcSize < COPY_THRESHOLD || (src instanceof RandomAccess && dest instanceof RandomAccess)) {
			for (int i = 0; i < srcSize; i++)
				dest.set(i, src.get(i));
		} else { // 一个链表一个线性表也可以用迭代器赋值
			ListIterator<? super T> di = dest.listIterator();
			ListIterator<? extends T> si = src.listIterator();
			for (int i = 0; i < srcSize; i++) {
				di.next();
				di.set(si.next());
			}
		}
	}
 
	/**
	 * 
	 * 返回集合中的最小元素。前提是其中的元素都是可比的，即实现了Comparable接口 找出一个通用的算法其实不容易，尽管它的思想不难。
	 * 反正要依次遍历完所有元素，所以直接用了迭代器
	 */
	public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll) {
		Iterator<? extends T> i = coll.iterator();
		T candidate = i.next();
		while (i.hasNext()) {
			T next = i.next();
			if (next.compareTo(candidate) < 0)
				candidate = next;
		}
		return candidate;
	}
 
	/**
	 * 根据提供的比较器求最小元素
	 */
	public static <T> T min(Collection<? extends T> coll, Comparator<? super T> comp) {
		if (comp == null)
			// 返回默认比较器，其实默认比较器什么也不做，只是看集合元素是否实现了Comparable接口，
			// 否则抛出ClassCastException
			return (T) min((Collection<SelfComparable>) (Collection) coll);
 
		Iterator<? extends T> i = coll.iterator();
		T candidate = i.next();
		// 假设第一个元素为最小元素
 
		while (i.hasNext()) {
			T next = i.next();
			if (comp.compare(next, candidate) < 0)
				candidate = next;
		}
		return candidate;
	}
 
	/**
	 * 求集合中最大元素
	 */
	public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> coll) {
		Iterator<? extends T> i = coll.iterator();
		T candidate = i.next();
 
		while (i.hasNext()) {
			T next = i.next();
			if (next.compareTo(candidate) > 0)
				candidate = next;
		}
		return candidate;
	}
 
	/**
	 * 根据指定比较器求集合中最大元素
	 */
	public static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp) {
		if (comp == null)
			return (T) max((Collection<SelfComparable>) (Collection) coll);
 
		Iterator<? extends T> i = coll.iterator();
		T candidate = i.next();
 
		while (i.hasNext()) {
			T next = i.next();
			if (comp.compare(next, candidate) > 0)
				candidate = next;
		}
		return candidate;
	}
 
	/**
	 * 
	 * 旋转移位List中的元素通过指定的distance。每个元素移动后的位置为： (i +
	 * distance)%list.size.此方法不会改变列表的长度
	 * 
	 * 比如，类表元素为： [t, a, n, k, s , w] 执行Collections.rotate(list, 2)或
	 * Collections.rotate(list, -4)后, list中的元素将变为 [s, w, t, a, n ,
	 * k]。可以这样理解：正数表示向后移，负数表示向前移
	 *
	 */
	public static void rotate(List<?> list, int distance) {
		if (list instanceof RandomAccess || list.size() < ROTATE_THRESHOLD)
			rotate1((List) list, distance);
		else
			rotate2((List) list, distance);
	}
 
	private static <T> void rotate1(List<T> list, int distance) {
		int size = list.size();
		if (size == 0)
			return;
		distance = distance % size; // distance始终处于0到size(不包括)之间
		if (distance < 0)
			distance += size; // 还是以向后移来计算的
		if (distance == 0)
			return;
 
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			T displaced = list.get(cycleStart);
			int i = cycleStart;
			do {
				i += distance; // 求新位置
				if (i >= size)
					i -= size; // 超出size就减去size
				displaced = list.set(i, displaced);
				// 为新位置赋原来的值
				nMoved++; // 如果等于size证明全部替换完毕
			} while (i != cycleStart); // 依次类推，求新位置的新位置
		}
	}
 
	private static void rotate2(List<?> list, int distance) {
		int size = list.size();
		if (size == 0)
			return;
		int mid = -distance % size;
		if (mid < 0)
			mid += size;
		if (mid == 0)
			return;
		// 好神奇啊
		reverse(list.subList(0, mid));
		reverse(list.subList(mid, size));
		reverse(list);
	}
 
	/**
	 * 
	 * 把指定集合中所有与oladVal相等的元素替换成newVal 只要list发生了改变就返回true
	 */
	public static <T> boolean replaceAll(List<T> list, T oldVal, T newVal) {
		boolean result = false;
		int size = list.size();
		if (size < REPLACEALL_THRESHOLD || list instanceof RandomAccess) {
			if (oldVal == null) {
				for (int i = 0; i < size; i++) {
					if (list.get(i) == null) {
						list.set(i, newVal);
						result = true;
					}
				}
			} else {
				for (int i = 0; i < size; i++) {
					if (oldVal.equals(list.get(i))) {
						list.set(i, newVal);
						result = true;
					}
				}
			}
		} else {
			ListIterator<T> itr = list.listIterator();
			if (oldVal == null) {
				for (int i = 0; i < size; i++) {
					if (itr.next() == null) {
						itr.set(newVal);
						result = true;
					}
				}
			} else {
				for (int i = 0; i < size; i++) {
					if (oldVal.equals(itr.next())) {
						itr.set(newVal);
						result = true;
					}
				}
			}
		}
		return result;
	}
 
	/**
	 * 
	 * target是否是source的子集，如果是返回target第一个元素的索引， 否则返回-1。
	 * 其实这里和串的模式匹配差不多。这里使用的是基本的回溯法。
	 * 
	 */
	public static int indexOfSubList(List<?> source, List<?> target) {
		int sourceSize = source.size();
		int targetSize = target.size();
		int maxCandidate = sourceSize - targetSize;
 
		if (sourceSize < INDEXOFSUBLIST_THRESHOLD
				|| (source instanceof RandomAccess && target instanceof RandomAccess)) {
			nextCand: for (int candidate = 0; candidate <= maxCandidate; candidate++) {
				for (int i = 0, j = candidate; i < targetSize; i++, j++)
					if (!eq(target.get(i), source.get(j)))
						continue nextCand; // 元素失配，跳到外部循环
				return candidate; // All elements of candidate matched target
			}
		} else { // Iterator version of above algorithm
			ListIterator<?> si = source.listIterator();
			nextCand: for (int candidate = 0; candidate <= maxCandidate; candidate++) {
				ListIterator<?> ti = target.listIterator();
				for (int i = 0; i < targetSize; i++) {
					if (!eq(ti.next(), si.next())) {
						// 回溯指针，然后跳到外部循环继续执行
						for (int j = 0; j < i; j++)
							si.previous();
						continue nextCand;
					}
				}
				return candidate;
			}
		}
		return -1; // 没有找到匹配的子串返回-1
	}
 
	/**
	 * 如果有一个或多个字串，返回最后一个出现的子串的第一个元素的索引
	 */
	public static int lastIndexOfSubList(List<?> source, List<?> target) {
		int sourceSize = source.size();
		int targetSize = target.size();
		int maxCandidate = sourceSize - targetSize;
 
		if (sourceSize < INDEXOFSUBLIST_THRESHOLD || source instanceof RandomAccess) { // Index
																						// access
																						// version
			nextCand: for (int candidate = maxCandidate; candidate >= 0; candidate--) {
				for (int i = 0, j = candidate; i < targetSize; i++, j++)
					if (!eq(target.get(i), source.get(j)))
						// 从source的maxCandidate位置开始比较。然后是maxCandidate-1，依次类推
						continue nextCand; // Element mismatch, try next cand
				return candidate; // All elements of candidate matched target
			}
		} else { // Iterator version of above algorithm
			if (maxCandidate < 0)
				return -1;
			ListIterator<?> si = source.listIterator(maxCandidate);
			nextCand: for (int candidate = maxCandidate; candidate >= 0; candidate--) {
				ListIterator<?> ti = target.listIterator();
				for (int i = 0; i < targetSize; i++) {
					if (!eq(ti.next(), si.next())) {
						if (candidate != 0) {
							// Back up source iterator to next candidate
							for (int j = 0; j <= i + 1; j++)
								si.previous();
						}
						continue nextCand;
					}
				}
				return candidate;
			}
		}
		return -1; // No candidate matched the target
	}
 
	// Unmodifiable Wrappers
 
	/**
	 * 
	 * 返回一个关于指定集合的不可修改的视图。 任何试图修改该视图的操作都将抛出一个UnsupportedOperationException
	 * 
	 * Collection返回的视图的equals方法不是调用底层集合的equals方法
	 * 而是继承了Object的equals方法。hashCode方法也是一样的。 因为Set和List的equals方法并不相同。
	 */
	public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> c) {
		return new UnmodifiableCollection<T>(c);
	}
 
	static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 1820017752578914078L;
 
		final Collection<? extends E> c;
 
		UnmodifiableCollection(Collection<? extends E> c) {
			if (c == null)
				throw new NullPointerException();
			this.c = c;
		}
 
		public int size() {
			return c.size();
		}
 
		public boolean isEmpty() {
			return c.isEmpty();
		}
 
		public boolean contains(Object o) {
			return c.contains(o);
		}
 
		public Object[] toArray() {
			return c.toArray();
		}
 
		public <T> T[] toArray(T[] a) {
			return c.toArray(a);
		}
 
		public String toString() {
			return c.toString();
		}
 
		public Iterator<E> iterator() {
			return new Iterator<E>() {
				Iterator<? extends E> i = c.iterator();
 
				public boolean hasNext() {
					return i.hasNext();
				}
 
				public E next() {
					return i.next();
				}
 
				public void remove() {
					// 试图修改集合的操作都将抛出此异常
					throw new UnsupportedOperationException();
				}
			};
		}
 
		public boolean add(E e) {
			throw new UnsupportedOperationException();
		}
 
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
 
		public boolean containsAll(Collection<?> coll) {
			return c.containsAll(coll);
		}
 
		public boolean addAll(Collection<? extends E> coll) {
			throw new UnsupportedOperationException();
		}
 
		public boolean removeAll(Collection<?> coll) {
			throw new UnsupportedOperationException();
		}
 
		public boolean retainAll(Collection<?> coll) {
			throw new UnsupportedOperationException();
		}
 
		public void clear() {
			throw new UnsupportedOperationException();
		}
	}
 
	/**
	 * 返回一个不可修改Set。 调用的是底层集合的equals方法
	 */
	public static <T> Set<T> unmodifiableSet(Set<? extends T> s) {
		return new UnmodifiableSet<T>(s);
	}
 
	/**
	 * @serial include
	 */
	static class UnmodifiableSet<E> extends UnmodifiableCollection<E> implements Set<E>, Serializable {
		private static final long serialVersionUID = -9215047833775013803L;
 
		UnmodifiableSet(Set<? extends E> s) {
			super(s);
		}
 
		public boolean equals(Object o) {
			return o == this || c.equals(o);
		}
 
		public int hashCode() {
			return c.hashCode();
		}
	}
 
	/**
	 * 返回一个不可修改的Sort Set
	 */
	public static <T> SortedSet<T> unmodifiableSortedSet(SortedSet<T> s) {
		return new UnmodifiableSortedSet<T>(s);
	}
 
	static class UnmodifiableSortedSet<E> extends UnmodifiableSet<E> implements SortedSet<E>, Serializable {
		private static final long serialVersionUID = -4929149591599911165L;
		private final SortedSet<E> ss;
 
		UnmodifiableSortedSet(SortedSet<E> s) {
			super(s);
			ss = s;
		}
 
		public Comparator<? super E> comparator() {
			return ss.comparator();
		}
 
		public SortedSet<E> subSet(E fromElement, E toElement) {
			return new UnmodifiableSortedSet<E>(ss.subSet(fromElement, toElement));
		}
 
		public SortedSet<E> headSet(E toElement) {
			return new UnmodifiableSortedSet<E>(ss.headSet(toElement));
		}
 
		public SortedSet<E> tailSet(E fromElement) {
			return new UnmodifiableSortedSet<E>(ss.tailSet(fromElement));
		}
 
		public E first() {
			return ss.first();
		}
 
		public E last() {
			return ss.last();
		}
	}
 
	/**
	 * 返回一个 不可修改的List 如果原List实现了RandomAccess接口，返回的List也将实现此接口
	 */
	public static <T> List<T> unmodifiableList(List<? extends T> list) {
		return (list instanceof RandomAccess ? new UnmodifiableRandomAccessList<T>(list)
				: new UnmodifiableList<T>(list));
	}
 
	static class UnmodifiableList<E> extends UnmodifiableCollection<E> implements List<E> {
		static final long serialVersionUID = -283967356065247728L;
		final List<? extends E> list;
 
		UnmodifiableList(List<? extends E> list) {
			super(list);
			this.list = list;
		}
 
		public boolean equals(Object o) {
			return o == this || list.equals(o);
		}
 
		public int hashCode() {
			return list.hashCode();
		}
 
		public E get(int index) {
			return list.get(index);
		}
 
		public E set(int index, E element) {
			throw new UnsupportedOperationException();
		}
 
		public void add(int index, E element) {
			throw new UnsupportedOperationException();
		}
 
		public E remove(int index) {
			throw new UnsupportedOperationException();
		}
 
		public int indexOf(Object o) {
			return list.indexOf(o);
		}
 
		public int lastIndexOf(Object o) {
			return list.lastIndexOf(o);
		}
 
		public boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}
 
		public ListIterator<E> listIterator() {
			return listIterator(0);
		}
 
		public ListIterator<E> listIterator(final int index) {
			return new ListIterator<E>() {
				ListIterator<? extends E> i = list.listIterator(index);
 
				public boolean hasNext() {
					return i.hasNext();
				}
 
				public E next() {
					return i.next();
				}
 
				public boolean hasPrevious() {
					return i.hasPrevious();
				}
 
				public E previous() {
					return i.previous();
				}
 
				public int nextIndex() {
					return i.nextIndex();
				}
 
				public int previousIndex() {
					return i.previousIndex();
				}
 
				public void remove() {
					throw new UnsupportedOperationException();
				}
 
				public void set(E e) {
					throw new UnsupportedOperationException();
				}
 
				public void add(E e) {
					throw new UnsupportedOperationException();
				}
			};
		}
 
		public List<E> subList(int fromIndex, int toIndex) {
			return new UnmodifiableList<E>(list.subList(fromIndex, toIndex));
		}
 
		/**
		 * UnmodifiableRandomAccessList instances are serialized as
		 * UnmodifiableList instances to allow them to be deserialized in
		 * pre-1.4 JREs (which do not have UnmodifiableRandomAccessList). This
		 * method inverts the transformation. As a beneficial side-effect, it
		 * also grafts the RandomAccess marker onto UnmodifiableList instances
		 * that were serialized in pre-1.4 JREs.
		 *
		 * Note: Unfortunately, UnmodifiableRandomAccessList instances
		 * serialized in 1.4.1 and deserialized in 1.4 will become
		 * UnmodifiableList instances, as this method was missing in 1.4.
		 * 这个，自己看吧...
		 */
		private Object readResolve() {
			return (list instanceof RandomAccess ? new UnmodifiableRandomAccessList<E>(list) : this);
		}
	}
 
	static class UnmodifiableRandomAccessList<E> extends UnmodifiableList<E> implements RandomAccess {
		UnmodifiableRandomAccessList(List<? extends E> list) {
			super(list);
		}
 
		public List<E> subList(int fromIndex, int toIndex) {
			return new UnmodifiableRandomAccessList<E>(list.subList(fromIndex, toIndex));
		}
 
		private static final long serialVersionUID = -2542308836966382001L;
 
		/**
		 * Allows instances to be deserialized in pre-1.4 JREs (which do not
		 * have UnmodifiableRandomAccessList). UnmodifiableList has a
		 * readResolve method that inverts this transformation upon
		 * deserialization.
		 */
		private Object writeReplace() {
			return new UnmodifiableList<E>(list);
		}
	}
 
	/**
	 * 返回一个不可修改的map
	 */
	public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> m) {
		return new UnmodifiableMap<K, V>(m);
	}
 
	private static class UnmodifiableMap<K, V> implements Map<K, V>, Serializable {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = -1034234728574286014L;
 
		private final Map<? extends K, ? extends V> m;
 
		UnmodifiableMap(Map<? extends K, ? extends V> m) {
			if (m == null)
				throw new NullPointerException();
			this.m = m;
		}
 
		public int size() {
			return m.size();
		}
 
		public boolean isEmpty() {
			return m.isEmpty();
		}
 
		public boolean containsKey(Object key) {
			return m.containsKey(key);
		}
 
		public boolean containsValue(Object val) {
			return m.containsValue(val);
		}
 
		public V get(Object key) {
			return m.get(key);
		}
 
		public V put(K key, V value) {
			throw new UnsupportedOperationException();
		}
 
		public V remove(Object key) {
			throw new UnsupportedOperationException();
		}
 
		public void putAll(Map<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}
 
		public void clear() {
			throw new UnsupportedOperationException();
		}
 
		private transient Set<K> keySet = null;
		private transient Set<Map.Entry<K, V>> entrySet = null;
		private transient Collection<V> values = null;
 
		// 返回的key集也是不可修改的
		public Set<K> keySet() {
			if (keySet == null)
				keySet = unmodifiableSet(m.keySet());
			return keySet;
		}
 
		// EntrySet被重新进行包装
		public Set<Map.Entry<K, V>> entrySet() {
			if (entrySet == null)
				entrySet = new UnmodifiableEntrySet<K, V>(m.entrySet());
			return entrySet;
		}
 
		public Collection<V> values() {
			if (values == null)
				values = unmodifiableCollection(m.values());
			return values;
		}
 
		public boolean equals(Object o) {
			return o == this || m.equals(o);
		}
 
		public int hashCode() {
			return m.hashCode();
		}
 
		public String toString() {
			return m.toString();
		}
 
		/**
		 * 
		 * 需要重新包装返回的EntrySet对象
		 */
		static class UnmodifiableEntrySet<K, V> extends UnmodifiableSet<Map.Entry<K, V>> {
			private static final long serialVersionUID = 7854390611657943733L;
 
			UnmodifiableEntrySet(Set<? extends Map.Entry<? extends K, ? extends V>> s) {
				super((Set) s);
			}
 
			public Iterator<Map.Entry<K, V>> iterator() {
				return new Iterator<Map.Entry<K, V>>() {
					// 父类UnmodifiableColletion的c
					Iterator<? extends Map.Entry<? extends K, ? extends V>> i = c.iterator();
 
					public boolean hasNext() {
						return i.hasNext();
					}
 
					public Map.Entry<K, V> next() {
						return new UnmodifiableEntry<K, V>(i.next());
					}
 
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
 
			public Object[] toArray() {
				Object[] a = c.toArray();
				for (int i = 0; i < a.length; i++)
					a[i] = new UnmodifiableEntry<K, V>((Map.Entry<K, V>) a[i]);
				return a;
			}
 
			public <T> T[] toArray(T[] a) {
 
				Object[] arr = c.toArray(a.length == 0 ? a : Arrays.copyOf(a, 0));
 
				for (int i = 0; i < arr.length; i++)
					arr[i] = new UnmodifiableEntry<K, V>((Map.Entry<K, V>) arr[i]);
 
				if (arr.length > a.length)
					return (T[]) arr;
 
				System.arraycopy(arr, 0, a, 0, arr.length);
				if (a.length > arr.length)
					a[arr.length] = null;
				return a;
			}
 
			public boolean contains(Object o) {
				if (!(o instanceof Map.Entry))
					return false;
				return c.contains(new UnmodifiableEntry<K, V>((Map.Entry<K, V>) o));
			}
 
			public boolean containsAll(Collection<?> coll) {
				Iterator<?> e = coll.iterator();
				while (e.hasNext())
					if (!contains(e.next())) // Invokes safe contains() above
						return false;
				return true;
			}
 
			public boolean equals(Object o) {
				if (o == this)
					return true;
 
				if (!(o instanceof Set))
					return false;
				Set s = (Set) o;
				if (s.size() != c.size())
					return false;
				return containsAll(s); // Invokes safe containsAll() above
			}
 
			/**
			 * 重新包装Entry。
			 */
			private static class UnmodifiableEntry<K, V> implements Map.Entry<K, V> {
				private Map.Entry<? extends K, ? extends V> e;
 
				UnmodifiableEntry(Map.Entry<? extends K, ? extends V> e) {
					this.e = e;
				}
 
				public K getKey() {
					return e.getKey();
				}
 
				public V getValue() {
					return e.getValue();
				}
 
				public V setValue(V value) { // 调用set方法将抛出一个异常
					throw new UnsupportedOperationException();
				}
 
				public int hashCode() {
					return e.hashCode();
				}
 
				public boolean equals(Object o) {
					if (!(o instanceof Map.Entry))
						return false;
					Map.Entry t = (Map.Entry) o;
					return eq(e.getKey(), t.getKey()) && eq(e.getValue(), t.getValue());
				}
 
				public String toString() {
					return e.toString();
				}
			}
		}
	}
 
	/**
	 * 返回一个不可修改的SortedMap
	 */
	public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> m) {
		return new UnmodifiableSortedMap<K, V>(m);
	}
 
	static class UnmodifiableSortedMap<K, V> extends UnmodifiableMap<K, V> implements SortedMap<K, V>, Serializable {
		private static final long serialVersionUID = -8806743815996713206L;
 
		private final SortedMap<K, ? extends V> sm;
 
		UnmodifiableSortedMap(SortedMap<K, ? extends V> m) {
			super(m);
			sm = m;
		}
 
		public Comparator<? super K> comparator() {
			return sm.comparator();
		}
 
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			return new UnmodifiableSortedMap<K, V>(sm.subMap(fromKey, toKey));
		}
 
		public SortedMap<K, V> headMap(K toKey) {
			return new UnmodifiableSortedMap<K, V>(sm.headMap(toKey));
		}
 
		public SortedMap<K, V> tailMap(K fromKey) {
			return new UnmodifiableSortedMap<K, V>(sm.tailMap(fromKey));
		}
 
		public K firstKey() {
			return sm.firstKey();
		}
 
		public K lastKey() {
			return sm.lastKey();
		}
	}
 
	// 同步包装
 
	/**
	 * 
	 * 返回一个线程安全的集合 但是当用户遍历此集合时，需要手动进行同步 Collection c =
	 * Collections.synchronizedCollection(myCollection); ... synchronized(c) {
	 * Iterator i = c.iterator(); // Must be in the synchronized block while
	 * (i.hasNext()) foo(i.next()); }
	 * 
	 */
	public static <T> Collection<T> synchronizedCollection(Collection<T> c) {
		return new SynchronizedCollection<T>(c);
	}
 
	static <T> Collection<T> synchronizedCollection(Collection<T> c, Object mutex) {
		return new SynchronizedCollection<T>(c, mutex);
	}
 
	/**
	 * @serial include
	 */
	static class SynchronizedCollection<E> implements Collection<E>, Serializable {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 3053995032091335093L;
 
		final Collection<E> c; // 返回的集合
		final Object mutex; // 需要同步的对象
 
		SynchronizedCollection(Collection<E> c) {
			if (c == null)
				throw new NullPointerException();
			this.c = c;
			mutex = this;
		}
 
		SynchronizedCollection(Collection<E> c, Object mutex) {
			this.c = c;
			this.mutex = mutex;
		}
 
		public int size() {
			synchronized (mutex) {
				return c.size();
			}
		}
 
		public boolean isEmpty() {
			synchronized (mutex) {
				return c.isEmpty();
			}
		}
 
		public boolean contains(Object o) {
			synchronized (mutex) {
				return c.contains(o);
			}
		}
 
		public Object[] toArray() {
			synchronized (mutex) {
				return c.toArray();
			}
		}
 
		public <T> T[] toArray(T[] a) {
			synchronized (mutex) {
				return c.toArray(a);
			}
		}
 
		public Iterator<E> iterator() {
			return c.iterator(); // 必须用户自己手动同步
		}
 
		public boolean add(E e) {
			synchronized (mutex) {
				return c.add(e);
			}
		}
 
		public boolean remove(Object o) {
			synchronized (mutex) {
				return c.remove(o);
			}
		}
 
		public boolean containsAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.containsAll(coll);
			}
		}
 
		public boolean addAll(Collection<? extends E> coll) {
			synchronized (mutex) {
				return c.addAll(coll);
			}
		}
 
		public boolean removeAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.removeAll(coll);
			}
		}
 
		public boolean retainAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.retainAll(coll);
			}
		}
 
		public void clear() {
			synchronized (mutex) {
				c.clear();
			}
		}
 
		public String toString() {
			synchronized (mutex) {
				return c.toString();
			}
		}
 
		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}
 
	/**
	 * 返回一个线程安全的Set
	 */
	public static <T> Set<T> synchronizedSet(Set<T> s) {
		return new SynchronizedSet<T>(s);
	}
 
	static <T> Set<T> synchronizedSet(Set<T> s, Object mutex) {
		return new SynchronizedSet<T>(s, mutex);
	}
 
	/**
	 * @serial include
	 */
	static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {
		private static final long serialVersionUID = 487447009682186044L;
 
		SynchronizedSet(Set<E> s) {
			super(s);
		}
 
		SynchronizedSet(Set<E> s, Object mutex) {
			super(s, mutex);
		}
 
		public boolean equals(Object o) {
			synchronized (mutex) {
				return c.equals(o);
			}
		}
 
		public int hashCode() {
			synchronized (mutex) {
				return c.hashCode();
			}
		}
	}
 
	/**
	 * 返回一个线程安全的SortedSet
	 */
	public static <T> SortedSet<T> synchronizedSortedSet(SortedSet<T> s) {
		return new SynchronizedSortedSet<T>(s);
	}
 
	/**
	 * @serial include
	 */
	static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E> {
		private static final long serialVersionUID = 8695801310862127406L;
 
		final private SortedSet<E> ss;
 
		SynchronizedSortedSet(SortedSet<E> s) {
			super(s);
			ss = s;
		}
 
		SynchronizedSortedSet(SortedSet<E> s, Object mutex) {
			super(s, mutex);
			ss = s;
		}
 
		public Comparator<? super E> comparator() {
			synchronized (mutex) {
				return ss.comparator();
			}
		}
 
		public SortedSet<E> subSet(E fromElement, E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(ss.subSet(fromElement, toElement), mutex);
			}
		}
 
		public SortedSet<E> headSet(E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(ss.headSet(toElement), mutex);
			}
		}
 
		public SortedSet<E> tailSet(E fromElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(ss.tailSet(fromElement), mutex);
			}
		}
 
		public E first() {
			synchronized (mutex) {
				return ss.first();
			}
		}
 
		public E last() {
			synchronized (mutex) {
				return ss.last();
			}
		}
	}
 
	/**
	 * 返回一个线程安全的List， 如果List是基于随机访问的，返回的List同样实现了RandomAccess接口
	 */
	public static <T> List<T> synchronizedList(List<T> list) {
		return (list instanceof RandomAccess ? new SynchronizedRandomAccessList<T>(list)
				: new SynchronizedList<T>(list));
	}
 
	static <T> List<T> synchronizedList(List<T> list, Object mutex) {
		return (list instanceof RandomAccess ? new SynchronizedRandomAccessList<T>(list, mutex)
				: new SynchronizedList<T>(list, mutex));
	}
 
	/**
	 * @serial include
	 */
	static class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E> {
		static final long serialVersionUID = -7754090372962971524L;
 
		final List<E> list;
 
		SynchronizedList(List<E> list) {
			super(list);
			this.list = list;
		}
 
		SynchronizedList(List<E> list, Object mutex) {
			super(list, mutex);
			this.list = list;
		}
 
		public boolean equals(Object o) {
			synchronized (mutex) {
				return list.equals(o);
			}
		}
 
		public int hashCode() {
			synchronized (mutex) {
				return list.hashCode();
			}
		}
 
		public E get(int index) {
			synchronized (mutex) {
				return list.get(index);
			}
		}
 
		public E set(int index, E element) {
			synchronized (mutex) {
				return list.set(index, element);
			}
		}
 
		public void add(int index, E element) {
			synchronized (mutex) {
				list.add(index, element);
			}
		}
 
		public E remove(int index) {
			synchronized (mutex) {
				return list.remove(index);
			}
		}
 
		public int indexOf(Object o) {
			synchronized (mutex) {
				return list.indexOf(o);
			}
		}
 
		public int lastIndexOf(Object o) {
			synchronized (mutex) {
				return list.lastIndexOf(o);
			}
		}
 
		public boolean addAll(int index, Collection<? extends E> c) {
			synchronized (mutex) {
				return list.addAll(index, c);
			}
		}
 
		public ListIterator<E> listIterator() {
			return list.listIterator(); // Must be manually synched by user
		}
 
		public ListIterator<E> listIterator(int index) {
			return list.listIterator(index); // Must be manually synched by user
		}
 
		public List<E> subList(int fromIndex, int toIndex) {
			synchronized (mutex) {
				return new SynchronizedList<E>(list.subList(fromIndex, toIndex), mutex);
			}
		}
 
		private Object readResolve() {
			return (list instanceof RandomAccess ? new SynchronizedRandomAccessList<E>(list) : this);
		}
	}
 
	/**
	 * @serial include
	 */
	static class SynchronizedRandomAccessList<E> extends SynchronizedList<E> implements RandomAccess {
 
		SynchronizedRandomAccessList(List<E> list) {
			super(list);
		}
 
		SynchronizedRandomAccessList(List<E> list, Object mutex) {
			super(list, mutex);
		}
 
		public List<E> subList(int fromIndex, int toIndex) {
			synchronized (mutex) {
				return new SynchronizedRandomAccessList<E>(list.subList(fromIndex, toIndex), mutex);
			}
		}
 
		static final long serialVersionUID = 1530674583602358482L;
 
		private Object writeReplace() {
			return new SynchronizedList<E>(list);
		}
	}
 
	/**
	 * 返回一个线程安全的map
	 */
	public static <K, V> Map<K, V> synchronizedMap(Map<K, V> m) {
		return new SynchronizedMap<K, V>(m);
	}
 
	/**
	 * @serial include
	 */
	private static class SynchronizedMap<K, V> implements Map<K, V>, Serializable {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 1978198479659022715L;
 
		private final Map<K, V> m; // Backing Map
		final Object mutex; // Object on which to synchronize
 
		SynchronizedMap(Map<K, V> m) {
			if (m == null)
				throw new NullPointerException();
			this.m = m;
			mutex = this;
		}
 
		SynchronizedMap(Map<K, V> m, Object mutex) {
			this.m = m;
			this.mutex = mutex;
		}
 
		public int size() {
			synchronized (mutex) {
				return m.size();
			}
		}
 
		public boolean isEmpty() {
			synchronized (mutex) {
				return m.isEmpty();
			}
		}
 
		public boolean containsKey(Object key) {
			synchronized (mutex) {
				return m.containsKey(key);
			}
		}
 
		public boolean containsValue(Object value) {
			synchronized (mutex) {
				return m.containsValue(value);
			}
		}
 
		public V get(Object key) {
			synchronized (mutex) {
				return m.get(key);
			}
		}
 
		public V put(K key, V value) {
			synchronized (mutex) {
				return m.put(key, value);
			}
		}
 
		public V remove(Object key) {
			synchronized (mutex) {
				return m.remove(key);
			}
		}
 
		public void putAll(Map<? extends K, ? extends V> map) {
			synchronized (mutex) {
				m.putAll(map);
			}
		}
 
		public void clear() {
			synchronized (mutex) {
				m.clear();
			}
		}
 
		private transient Set<K> keySet = null;
		private transient Set<Map.Entry<K, V>> entrySet = null;
		private transient Collection<V> values = null;
 
		public Set<K> keySet() {
			synchronized (mutex) {
				if (keySet == null)
					keySet = new SynchronizedSet<K>(m.keySet(), mutex);
				return keySet;
			}
		}
 
		public Set<Map.Entry<K, V>> entrySet() {
			synchronized (mutex) {
				if (entrySet == null)
					entrySet = new SynchronizedSet<Map.Entry<K, V>>(m.entrySet(), mutex);
				return entrySet;
			}
		}
 
		public Collection<V> values() {
			synchronized (mutex) {
				if (values == null)
					values = new SynchronizedCollection<V>(m.values(), mutex);
				return values;
			}
		}
 
		public boolean equals(Object o) {
			synchronized (mutex) {
				return m.equals(o);
			}
		}
 
		public int hashCode() {
			synchronized (mutex) {
				return m.hashCode();
			}
		}
 
		public String toString() {
			synchronized (mutex) {
				return m.toString();
			}
		}
 
		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}
 
	/**
	 * 返回一个线程安全的SortedSet
	 */
	public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> m) {
		return new SynchronizedSortedMap<K, V>(m);
	}
 
	/**
	 * @serial include
	 */
	static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V> {
		private static final long serialVersionUID = -8798146769416483793L;
 
		private final SortedMap<K, V> sm;
 
		SynchronizedSortedMap(SortedMap<K, V> m) {
			super(m);
			sm = m;
		}
 
		SynchronizedSortedMap(SortedMap<K, V> m, Object mutex) {
			super(m, mutex);
			sm = m;
		}
 
		public Comparator<? super K> comparator() {
			synchronized (mutex) {
				return sm.comparator();
			}
		}
 
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<K, V>(sm.subMap(fromKey, toKey), mutex);
			}
		}
 
		public SortedMap<K, V> headMap(K toKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<K, V>(sm.headMap(toKey), mutex);
			}
		}
 
		public SortedMap<K, V> tailMap(K fromKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<K, V>(sm.tailMap(fromKey), mutex);
			}
		}
 
		public K firstKey() {
			synchronized (mutex) {
				return sm.firstKey();
			}
		}
 
		public K lastKey() {
			synchronized (mutex) {
				return sm.lastKey();
			}
		}
	}
 
	// Dynamically typesafe collection wrappers
 
	/**
	 * 
	 * 返回一个动态的类型安全的集合。任何试图插入错误类型的元素的操作将立刻抛出 ClassCastException
	 * 动态类型安全视图的一个主要作用是用作debug调试， 因为它能正确反映出出错的位置。 例如：ArrayList<String> strings =
	 * new ArrayList<String>(); ArrayList rawList = strings; rawList.add(new
	 * Date()); add方法并不进行类型检查，所以存入了非String的对象。只有在重新获取该对象 转化为String类型的时候才抛出异常。
	 * 而动态类型安全的集合能在add时就会抛出ClassCastException。 这种方法的优点是错误可以在正确的位置被报告
	 * 
	 *
	 */
	public static <E> Collection<E> checkedCollection(Collection<E> c, Class<E> type) {
		return new CheckedCollection<E>(c, type);
	}
 
	/**
	 * @serial include
	 */
	static class CheckedCollection<E> implements Collection<E>, Serializable {
		private static final long serialVersionUID = 1578914078182001775L;
 
		final Collection<E> c;
		final Class<E> type;
 
		void typeCheck(Object o) {
			if (!type.isInstance(o)) // o是否能被转换成type类型
				throw new ClassCastException(
						"Attempt to insert " + o.getClass() + " element into collection with element type " + type);
		}
 
		CheckedCollection(Collection<E> c, Class<E> type) {
			if (c == null || type == null)
				throw new NullPointerException();
			this.c = c;
			this.type = type;
		}
 
		public int size() {
			return c.size();
		}
 
		public boolean isEmpty() {
			return c.isEmpty();
		}
 
		public boolean contains(Object o) {
			return c.contains(o);
		}
 
		public Object[] toArray() {
			return c.toArray();
		}
 
		public <T> T[] toArray(T[] a) {
			return c.toArray(a);
		}
 
		public String toString() {
			return c.toString();
		}
 
		public boolean remove(Object o) {
			return c.remove(o);
		}
 
		public boolean containsAll(Collection<?> coll) {
			return c.containsAll(coll);
		}
 
		public boolean removeAll(Collection<?> coll) {
			return c.removeAll(coll);
		}
 
		public boolean retainAll(Collection<?> coll) {
			return c.retainAll(coll);
		}
 
		public void clear() {
			c.clear();
		}
 
		public Iterator<E> iterator() {
			return new Iterator<E>() {
				private final Iterator<E> it = c.iterator();
 
				public boolean hasNext() {
					return it.hasNext();
				}
 
				public E next() {
					return it.next();
				}
 
				public void remove() {
					it.remove();
				}
			};
		}
 
		public boolean add(E e) {
			typeCheck(e); // 添加元素需要进行类型检查
			return c.add(e);
		}
 
		public boolean addAll(Collection<? extends E> coll) {
			E[] a = null;
			try {
				a = coll.toArray(zeroLengthElementArray());
				// 根据zero数组的类型来转换集合为数组。如果coll中含有其他类型这里就会抛出异常
			} catch (ArrayStoreException e) {
				throw new ClassCastException();
			}
 
			boolean result = false;
			for (E e : a)
				result |= c.add(e); // 只要集合发生了改变就返回true
			return result;
		}
 
		private E[] zeroLengthElementArray = null; // Lazily initialized
 
		/*
		 * We don't need locking or volatile, because it's OK if we create
		 * several zeroLengthElementArrays, and they're immutable.
		 */
		E[] zeroLengthElementArray() {
			if (zeroLengthElementArray == null)
				zeroLengthElementArray = (E[]) Array.newInstance(type, 0);
			return zeroLengthElementArray;
		}
	}
 
	/**
	 * 返回一个会检查类型的集合Set
	 */
	public static <E> Set<E> checkedSet(Set<E> s, Class<E> type) {
		return new CheckedSet<E>(s, type);
	}
 
	/**
	 * @serial include
	 */
	static class CheckedSet<E> extends CheckedCollection<E> implements Set<E>, Serializable {
		private static final long serialVersionUID = 4694047833775013803L;
 
		CheckedSet(Set<E> s, Class<E> elementType) {
			super(s, elementType);
		}
 
		public boolean equals(Object o) {
			return o == this || c.equals(o);
		}
 
		public int hashCode() {
			return c.hashCode();
		}
	}
 
	/**
	 * 返回一个类型安全的集合SortedSet
	 */
	public static <E> SortedSet<E> checkedSortedSet(SortedSet<E> s, Class<E> type) {
		return new CheckedSortedSet<E>(s, type);
	}
 
	/**
	 * @serial include
	 */
	static class CheckedSortedSet<E> extends CheckedSet<E> implements SortedSet<E>, Serializable {
		private static final long serialVersionUID = 1599911165492914959L;
		private final SortedSet<E> ss;
 
		CheckedSortedSet(SortedSet<E> s, Class<E> type) {
			super(s, type);
			ss = s;
		}
 
		public Comparator<? super E> comparator() {
			return ss.comparator();
		}
 
		public E first() {
			return ss.first();
		}
 
		public E last() {
			return ss.last();
		}
 
		public SortedSet<E> subSet(E fromElement, E toElement) {
			return new CheckedSortedSet<E>(ss.subSet(fromElement, toElement), type);
		}
 
		public SortedSet<E> headSet(E toElement) {
			return new CheckedSortedSet<E>(ss.headSet(toElement), type);
		}
 
		public SortedSet<E> tailSet(E fromElement) {
			return new CheckedSortedSet<E>(ss.tailSet(fromElement), type);
		}
	}
 
	/**
	 * 返回一个类型安全的集合List
	 */
	public static <E> List<E> checkedList(List<E> list, Class<E> type) {
		return (list instanceof RandomAccess ? new CheckedRandomAccessList<E>(list, type)
				: new CheckedList<E>(list, type));
	}
 
	/**
	 * @serial include
	 */
	static class CheckedList<E> extends CheckedCollection<E> implements List<E> {
		static final long serialVersionUID = 65247728283967356L;
		final List<E> list;
 
		CheckedList(List<E> list, Class<E> type) {
			super(list, type);
			this.list = list;
		}
 
		public boolean equals(Object o) {
			return o == this || list.equals(o);
		}
 
		public int hashCode() {
			return list.hashCode();
		}
 
		public E get(int index) {
			return list.get(index);
		}
 
		public E remove(int index) {
			return list.remove(index);
		}
 
		public int indexOf(Object o) {
			return list.indexOf(o);
		}
 
		public int lastIndexOf(Object o) {
			return list.lastIndexOf(o);
		}
 
		public E set(int index, E element) {
			typeCheck(element);
			return list.set(index, element);
		}
 
		public void add(int index, E element) {
			typeCheck(element);
			list.add(index, element);
		}
 
		public boolean addAll(int index, Collection<? extends E> c) {
			// See CheckCollection.addAll, above, for an explanation
			E[] a = null;
			try {
				a = c.toArray(zeroLengthElementArray());
			} catch (ArrayStoreException e) {
				throw new ClassCastException();
			}
 
			return list.addAll(index, Arrays.asList(a));
		}
 
		public ListIterator<E> listIterator() {
			return listIterator(0);
		}
 
		public ListIterator<E> listIterator(final int index) {
			return new ListIterator<E>() {
				ListIterator<E> i = list.listIterator(index);
 
				public boolean hasNext() {
					return i.hasNext();
				}
 
				public E next() {
					return i.next();
				}
 
				public boolean hasPrevious() {
					return i.hasPrevious();
				}
 
				public E previous() {
					return i.previous();
				}
 
				public int nextIndex() {
					return i.nextIndex();
				}
 
				public int previousIndex() {
					return i.previousIndex();
				}
 
				public void remove() {
					i.remove();
				}
 
				public void set(E e) {
					typeCheck(e);
					i.set(e);
				}
 
				public void add(E e) {
					typeCheck(e);
					i.add(e);
				}
			};
		}
 
		public List<E> subList(int fromIndex, int toIndex) {
			return new CheckedList<E>(list.subList(fromIndex, toIndex), type);
		}
	}
 
	/**
	 * @serial include
	 */
	static class CheckedRandomAccessList<E> extends CheckedList<E> implements RandomAccess {
		private static final long serialVersionUID = 1638200125423088369L;
 
		CheckedRandomAccessList(List<E> list, Class<E> type) {
			super(list, type);
		}
 
		public List<E> subList(int fromIndex, int toIndex) {
			return new CheckedRandomAccessList<E>(list.subList(fromIndex, toIndex), type);
		}
	}
 
	/**
	 * 返回一个类型安全的集合Map
	 */
	public static <K, V> Map<K, V> checkedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
		return new CheckedMap<K, V>(m, keyType, valueType);
	}
 
	/**
	 * @serial include
	 */
	private static class CheckedMap<K, V> implements Map<K, V>, Serializable {
		private static final long serialVersionUID = 5742860141034234728L;
 
		private final Map<K, V> m;
		final Class<K> keyType;
		final Class<V> valueType;
 
		// 需要对key与value都进行类型检查
		private void typeCheck(Object key, Object value) {
			if (!keyType.isInstance(key))
				throw new ClassCastException(
						"Attempt to insert " + key.getClass() + " key into collection with key type " + keyType);
 
			if (!valueType.isInstance(value))
				throw new ClassCastException("Attempt to insert " + value.getClass()
						+ " value into collection with value type " + valueType);
		}
 
		CheckedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
			if (m == null || keyType == null || valueType == null)
				throw new NullPointerException();
			this.m = m;
			this.keyType = keyType;
			this.valueType = valueType;
		}
 
		public int size() {
			return m.size();
		}
 
		public boolean isEmpty() {
			return m.isEmpty();
		}
 
		public boolean containsKey(Object key) {
			return m.containsKey(key);
		}
 
		public boolean containsValue(Object v) {
			return m.containsValue(v);
		}
 
		public V get(Object key) {
			return m.get(key);
		}
 
		public V remove(Object key) {
			return m.remove(key);
		}
 
		public void clear() {
			m.clear();
		}
 
		public Set<K> keySet() {
			return m.keySet();
		}
 
		public Collection<V> values() {
			return m.values();
		}
 
		public boolean equals(Object o) {
			return o == this || m.equals(o);
		}
 
		public int hashCode() {
			return m.hashCode();
		}
 
		public String toString() {
			return m.toString();
		}
 
		public V put(K key, V value) {
			typeCheck(key, value);
			return m.put(key, value);
		}
 
		public void putAll(Map<? extends K, ? extends V> t) {
			// See CheckCollection.addAll, above, for an explanation
			K[] keys = null;
			try {
				keys = t.keySet().toArray(zeroLengthKeyArray());
			} catch (ArrayStoreException e) {
				throw new ClassCastException();
			}
			V[] values = null;
			try {
				values = t.values().toArray(zeroLengthValueArray());
			} catch (ArrayStoreException e) {
				throw new ClassCastException();
			}
 
			if (keys.length != values.length)
				throw new ConcurrentModificationException();
 
			for (int i = 0; i < keys.length; i++)
				m.put(keys[i], values[i]);
		}
 
		// Lazily initialized
		private K[] zeroLengthKeyArray = null;
		private V[] zeroLengthValueArray = null;
 
		/*
		 * We don't need locking or volatile, because it's OK if we create
		 * several zeroLengthValueArrays, and they're immutable.
		 */
		private K[] zeroLengthKeyArray() {
			if (zeroLengthKeyArray == null)
				zeroLengthKeyArray = (K[]) Array.newInstance(keyType, 0);
			return zeroLengthKeyArray;
		}
 
		private V[] zeroLengthValueArray() {
			if (zeroLengthValueArray == null)
				zeroLengthValueArray = (V[]) Array.newInstance(valueType, 0);
			return zeroLengthValueArray;
		}
 
		private transient Set<Map.Entry<K, V>> entrySet = null;
 
		public Set<Map.Entry<K, V>> entrySet() {
			if (entrySet == null)
				entrySet = new CheckedEntrySet<K, V>(m.entrySet(), valueType);
			return entrySet;
		}
 
		/**
		 * We need this class in addition to CheckedSet as Map.Entry permits
		 * modification of the backing Map via the setValue operation. This
		 * class is subtle: there are many possible attacks that must be
		 * thwarted.
		 *
		 * @serial exclude
		 */
		static class CheckedEntrySet<K, V> implements Set<Map.Entry<K, V>> {
			Set<Map.Entry<K, V>> s;
			Class<V> valueType;
 
			CheckedEntrySet(Set<Map.Entry<K, V>> s, Class<V> valueType) {
				this.s = s;
				this.valueType = valueType;
			}
 
			public int size() {
				return s.size();
			}
 
			public boolean isEmpty() {
				return s.isEmpty();
			}
 
			public String toString() {
				return s.toString();
			}
 
			public int hashCode() {
				return s.hashCode();
			}
 
			public boolean remove(Object o) {
				return s.remove(o);
			}
 
			public boolean removeAll(Collection<?> coll) {
				return s.removeAll(coll);
			}
 
			public boolean retainAll(Collection<?> coll) {
				return s.retainAll(coll);
			}
 
			public void clear() {
				s.clear();
			}
 
			public boolean add(Map.Entry<K, V> e) {
				throw new UnsupportedOperationException();
			}
 
			public boolean addAll(Collection<? extends Map.Entry<K, V>> coll) {
				throw new UnsupportedOperationException();
			}
 
			public Iterator<Map.Entry<K, V>> iterator() {
				return new Iterator<Map.Entry<K, V>>() {
					Iterator<Map.Entry<K, V>> i = s.iterator();
 
					public boolean hasNext() {
						return i.hasNext();
					}
 
					public void remove() {
						i.remove();
					}
 
					public Map.Entry<K, V> next() {
						return new CheckedEntry<K, V>(i.next(), valueType);
					}
				};
			}
 
			public Object[] toArray() {
				Object[] source = s.toArray();
 
				/*
				 * Ensure that we don't get an ArrayStoreException even if
				 * s.toArray returns an array of something other than Object
				 */
				Object[] dest = (CheckedEntry.class.isInstance(source.getClass().getComponentType()) ? source
						: new Object[source.length]);
 
				for (int i = 0; i < source.length; i++)
					dest[i] = new CheckedEntry<K, V>((Map.Entry<K, V>) source[i], valueType);
				return dest;
			}
 
			public <T> T[] toArray(T[] a) {
				// We don't pass a to s.toArray, to avoid window of
				// vulnerability wherein an unscrupulous multithreaded client
				// could get his hands on raw (unwrapped) Entries from s.
				Object[] arr = s.toArray(a.length == 0 ? a : Arrays.copyOf(a, 0));
 
				for (int i = 0; i < arr.length; i++)
					arr[i] = new CheckedEntry<K, V>((Map.Entry<K, V>) arr[i], valueType);
				if (arr.length > a.length)
					return (T[]) arr;
 
				System.arraycopy(arr, 0, a, 0, arr.length);
				if (a.length > arr.length)
					a[arr.length] = null;
				return a;
			}
 
			/**
			 * This method is overridden to protect the backing set against an
			 * object with a nefarious equals function that senses that the
			 * equality-candidate is Map.Entry and calls its setValue method.
			 */
			public boolean contains(Object o) {
				if (!(o instanceof Map.Entry))
					return false;
				return s.contains(new CheckedEntry<K, V>((Map.Entry<K, V>) o, valueType));
			}
 
			/**
			 * The next two methods are overridden to protect against an
			 * unscrupulous collection whose contains(Object o) method senses
			 * when o is a Map.Entry, and calls o.setValue.
			 */
			public boolean containsAll(Collection<?> coll) {
				Iterator<?> e = coll.iterator();
				while (e.hasNext())
					if (!contains(e.next())) // Invokes safe contains() above
						return false;
				return true;
			}
 
			public boolean equals(Object o) {
				if (o == this)
					return true;
				if (!(o instanceof Set))
					return false;
				Set<?> that = (Set<?>) o;
				if (that.size() != s.size())
					return false;
				return containsAll(that); // Invokes safe containsAll() above
			}
 
			/**
			 * This "wrapper class" serves two purposes: it prevents the client
			 * from modifying the backing Map, by short-circuiting the setValue
			 * method, and it protects the backing Map against an ill-behaved
			 * Map.Entry that attempts to modify another Map Entry when asked to
			 * perform an equality check.
			 */
			private static class CheckedEntry<K, V> implements Map.Entry<K, V> {
				private Map.Entry<K, V> e;
				private Class<V> valueType;
 
				CheckedEntry(Map.Entry<K, V> e, Class<V> valueType) {
					this.e = e;
					this.valueType = valueType;
				}
 
				public K getKey() {
					return e.getKey();
				}
 
				public V getValue() {
					return e.getValue();
				}
 
				public int hashCode() {
					return e.hashCode();
				}
 
				public String toString() {
					return e.toString();
				}
 
				public V setValue(V value) {
					if (!valueType.isInstance(value))
						throw new ClassCastException("Attempt to insert " + value.getClass()
								+ " value into collection with value type " + valueType);
					return e.setValue(value);
				}
 
				public boolean equals(Object o) {
					if (!(o instanceof Map.Entry))
						return false;
					Map.Entry t = (Map.Entry) o;
					return eq(e.getKey(), t.getKey()) && eq(e.getValue(), t.getValue());
				}
			}
		}
	}
 
	/**
	 * 返回一个类型安全的集合SortedMap
	 */
	public static <K, V> SortedMap<K, V> checkedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
		return new CheckedSortedMap<K, V>(m, keyType, valueType);
	}
 
	/**
	 * @serial include
	 */
	static class CheckedSortedMap<K, V> extends CheckedMap<K, V> implements SortedMap<K, V>, Serializable {
		private static final long serialVersionUID = 1599671320688067438L;
 
		private final SortedMap<K, V> sm;
 
		CheckedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
			super(m, keyType, valueType);
			sm = m;
		}
 
		public Comparator<? super K> comparator() {
			return sm.comparator();
		}
 
		public K firstKey() {
			return sm.firstKey();
		}
 
		public K lastKey() {
			return sm.lastKey();
		}
 
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			return new CheckedSortedMap<K, V>(sm.subMap(fromKey, toKey), keyType, valueType);
		}
 
		public SortedMap<K, V> headMap(K toKey) {
			return new CheckedSortedMap<K, V>(sm.headMap(toKey), keyType, valueType);
		}
 
		public SortedMap<K, V> tailMap(K fromKey) {
			return new CheckedSortedMap<K, V>(sm.tailMap(fromKey), keyType, valueType);
		}
	}
 
	// 其他
 
	/**
	 * 不可变的空集
	 */
	public static final Set EMPTY_SET = new EmptySet();
 
	/**
	 *
	 * 返回一个不可变的空集 size始终为0
	 */
	public static final <T> Set<T> emptySet() {
		return (Set<T>) EMPTY_SET;
	}
 
	/**
	 * @serial include
	 */
	private static class EmptySet extends AbstractSet<Object> implements Serializable {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 1582296315990362920L;
 
		public Iterator<Object> iterator() {
			return new Iterator<Object>() {
				public boolean hasNext() {
					return false;
				}
 
				public Object next() {
					throw new NoSuchElementException();
				}
 
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
 
		public int size() {
			return 0;
		}
 
		public boolean contains(Object obj) {
			return false;
		}
 
		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_SET;
		}
	}
 
	public static final List EMPTY_LIST = new EmptyList();
 
	public static final <T> List<T> emptyList() {
		return (List<T>) EMPTY_LIST;
	}
 
	/**
	 * @serial include
	 */
	private static class EmptyList extends AbstractList<Object> implements RandomAccess, Serializable {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 8842843931221139166L;
 
		public int size() {
			return 0;
		}
 
		public boolean contains(Object obj) {
			return false;
		}
 
		public Object get(int index) {
			throw new IndexOutOfBoundsException("Index: " + index);
		}
 
		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_LIST;
		}
	}
 
	public static final Map EMPTY_MAP = new EmptyMap();
 
	public static final <K, V> Map<K, V> emptyMap() {
		return (Map<K, V>) EMPTY_MAP;
	}
 
	private static class EmptyMap extends AbstractMap<Object, Object> implements Serializable {
 
		private static final long serialVersionUID = 6428348081105594320L;
 
		public int size() {
			return 0;
		}
 
		public boolean isEmpty() {
			return true;
		}
 
		public boolean containsKey(Object key) {
			return false;
		}
 
		public boolean containsValue(Object value) {
			return false;
		}
 
		public Object get(Object key) {
			return null;
		}
 
		public Set<Object> keySet() {
			return Collections.<Object> emptySet();
		}
 
		public Collection<Object> values() {
			return Collections.<Object> emptySet();
		}
 
		public Set<Map.Entry<Object, Object>> entrySet() {
			return Collections.emptySet();
		}
 
		public boolean equals(Object o) {
			return (o instanceof Map) && ((Map) o).size() == 0;
		}
 
		public int hashCode() {
			return 0;
		}
 
		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_MAP;
		}
	}
 
	/**
	 * 
	 * 返回只包含一个元素的不可变的集合
	 */
	public static <T> Set<T> singleton(T o) {
		return new SingletonSet<T>(o);
	}
 
	/**
	 * @serial include
	 */
	private static class SingletonSet<E> extends AbstractSet<E> implements Serializable {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 3193687207550431679L;
 
		final private E element;
 
		SingletonSet(E e) {
			element = e;
		}
 
		public Iterator<E> iterator() {
			return new Iterator<E>() {
				private boolean hasNext = true;
 
				public boolean hasNext() {
					return hasNext;
				}
 
				public E next() {
					if (hasNext) {
						hasNext = false;
						return element;
					}
					throw new NoSuchElementException();
				}
 
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
 
		public int size() {
			return 1;
		}
 
		public boolean contains(Object o) {
			return eq(o, element);
		}
	}
 
	public static <T> List<T> singletonList(T o) {
		return new SingletonList<T>(o);
	}
 
	private static class SingletonList<E> extends AbstractList<E> implements RandomAccess, Serializable {
 
		static final long serialVersionUID = 3093736618740652951L;
 
		private final E element;
 
		SingletonList(E obj) {
			element = obj;
		}
 
		public int size() {
			return 1;
		}
 
		public boolean contains(Object obj) {
			return eq(obj, element);
		}
 
		public E get(int index) {
			if (index != 0)
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");
			return element;
		}
	}
 
	public static <K, V> Map<K, V> singletonMap(K key, V value) {
		return new SingletonMap<K, V>(key, value);
	}
 
	private static class SingletonMap<K, V> extends AbstractMap<K, V> implements Serializable {
		private static final long serialVersionUID = -6979724477215052911L;
 
		private final K k;
		private final V v;
 
		SingletonMap(K key, V value) {
			k = key;
			v = value;
		}
 
		public int size() {
			return 1;
		}
 
		public boolean isEmpty() {
			return false;
		}
 
		public boolean containsKey(Object key) {
			return eq(key, k);
		}
 
		public boolean containsValue(Object value) {
			return eq(value, v);
		}
 
		public V get(Object key) {
			return (eq(key, k) ? v : null);
		}
 
		private transient Set<K> keySet = null;
		private transient Set<Map.Entry<K, V>> entrySet = null;
		private transient Collection<V> values = null;
 
		public Set<K> keySet() {
			if (keySet == null)
				keySet = singleton(k);
			return keySet;
		}
 
		public Set<Map.Entry<K, V>> entrySet() {
			if (entrySet == null)
				entrySet = Collections.<Map.Entry<K, V>> singleton(new SimpleImmutableEntry<K, V>(k, v));
			return entrySet;
		}
 
		public Collection<V> values() {
			if (values == null)
				values = singleton(v);
			return values;
		}
 
	}
 
	/**
	 * 
	 * 返回一个包含N个o元素的比可变的集合
	 * 
	 * @param n
	 *            添加的指定元素的个数
	 * @param o
	 *            被重复添加的元素
	 */
	public static <T> List<T> nCopies(int n, T o) {
		if (n < 0)
			throw new IllegalArgumentException("List length = " + n);
		return new CopiesList<T>(n, o);
	}
 
	/**
	 * @serial include
	 */
	private static class CopiesList<E> extends AbstractList<E> implements RandomAccess, Serializable {
		static final long serialVersionUID = 2739099268398711800L;
 
		final int n;
		final E element;
 
		CopiesList(int n, E e) {
			assert n >= 0;
			this.n = n;
			element = e;
		}
 
		public int size() {
			return n;
		}
 
		public boolean contains(Object obj) {
			return n != 0 && eq(obj, element);
		}
 
		public int indexOf(Object o) {
			return contains(o) ? 0 : -1;
		}
 
		public int lastIndexOf(Object o) {
			return contains(o) ? n - 1 : -1;
		}
 
		public E get(int index) {
			if (index < 0 || index >= n)
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + n);
			return element;
		}
 
		public Object[] toArray() {
			final Object[] a = new Object[n];
			if (element != null)
				Arrays.fill(a, 0, n, element);
			return a;
		}
 
		public <T> T[] toArray(T[] a) {
			final int n = this.n;
			if (a.length < n) {
				a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), n);
				if (element != null)
					Arrays.fill(a, 0, n, element);
			} else {
				Arrays.fill(a, 0, n, element);
				if (a.length > n)
					a[n] = null;
			}
			return a;
		}
 
		public List<E> subList(int fromIndex, int toIndex) {
			if (fromIndex < 0)
				throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
			if (toIndex > n)
				throw new IndexOutOfBoundsException("toIndex = " + toIndex);
			if (fromIndex > toIndex)
				throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
			return new CopiesList(toIndex - fromIndex, element);
		}
	}
 
	/**
	 * 返回一个比较器，该比较器能使集合按降序排列 例如： Arrays.sort(a, Collections.reverseOrder());
	 * 能按字母表相反的顺序排列数组
	 * 
	 */
	public static <T> Comparator<T> reverseOrder() {
		return (Comparator<T>) REVERSE_ORDER;
	}
 
	private static final Comparator REVERSE_ORDER = new ReverseComparator();
 
	/**
	 * @serial include
	 */
	private static class ReverseComparator<T> implements Comparator<Comparable<Object>>, Serializable {
 
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 7207038068494060240L;
 
		public int compare(Comparable<Object> c1, Comparable<Object> c2) {
			return c2.compareTo(c1);
		}
 
		private Object readResolve() {
			return reverseOrder();
		}
	}
 
	/**
	 * 根据指定比较器的相反顺序排列集合
	 */
	public static <T> Comparator<T> reverseOrder(Comparator<T> cmp) {
		if (cmp == null)
			return reverseOrder();
		return new ReverseComparator2<T>(cmp);
	}
 
	/**
	 * @serial include
	 */
	private static class ReverseComparator2<T> implements Comparator<T>, Serializable {
		private static final long serialVersionUID = 4374092139857L;
 
		private Comparator<T> cmp;
 
		ReverseComparator2(Comparator<T> cmp) {
			assert cmp != null;
			this.cmp = cmp;
		}
 
		public int compare(T t1, T t2) {
			return cmp.compare(t2, t1);
		}
	}
 
	/**
	 * 基于c之上返回一个枚举集
	 */
	public static <T> Enumeration<T> enumeration(final Collection<T> c) {
		return new Enumeration<T>() {
			Iterator<T> i = c.iterator();
 
			public boolean hasMoreElements() {
				return i.hasNext();
			}
 
			public T nextElement() {
				return i.next();
			}
		};
	}
 
	/**
	 * 根据枚举集中的元素返回一个ArrayList
	 */
	public static <T> ArrayList<T> list(Enumeration<T> e) {
		ArrayList<T> l = new ArrayList<T>();
		while (e.hasMoreElements())
			l.add(e.nextElement());
		return l;
	}
 
	/**
	 * 判断两对象是否相等或同位空
	 */
	private static boolean eq(Object o1, Object o2) {
		return (o1 == null ? o2 == null : o1.equals(o2));
	}
 
	/**
	 * 返回c中与o相等的元素的个数
	 */
	public static int frequency(Collection<?> c, Object o) {
		int result = 0;
		if (o == null) {
			for (Object e : c)
				if (e == null)
					result++;
		} else {
			for (Object e : c)
				if (o.equals(e))
					result++;
		}
		return result;
	}
 
	/**
	 * 
	 * 如果两指定集合没有共同的元素则返回true
	 */
	public static boolean disjoint(Collection<?> c1, Collection<?> c2) {
		/*
		 * 
		 * 优先遍历的始终是size小的集合或非Set的集合
		 */
		if ((c1 instanceof Set) && !(c2 instanceof Set) || (c1.size() > c2.size())) {
			Collection<?> tmp = c1;
			c1 = c2;
			c2 = tmp;
		}
 
		for (Object e : c1)
			if (c2.contains(e))
				return false;
		return true;
	}
 
	/**
	 * 把所有指定元素添加到集合c中， 有一个元素添加成功就返回true
	 */
	public static <T> boolean addAll(Collection<? super T> c, T... elements) {
		boolean result = false;
		for (T element : elements)
			result |= c.add(element);
		return result;
	}
 
	/**
	 * 根据指定的map返回一个set set存储的是map的键值
	 */
	public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
		return new SetFromMap<E>(map);
	}
 
	private static class SetFromMap<E> extends AbstractSet<E> implements Set<E>, Serializable {
		private final Map<E, Boolean> m; // The backing map
		private transient Set<E> s; // Its keySet
 
		SetFromMap(Map<E, Boolean> map) {
			if (!map.isEmpty())
				throw new IllegalArgumentException("Map is non-empty");
			m = map;
			s = map.keySet();
		}
 
		public void clear() {
			m.clear();
		}
 
		public int size() {
			return m.size();
		}
 
		public boolean isEmpty() {
			return m.isEmpty();
		}
 
		public boolean contains(Object o) {
			return m.containsKey(o);
		}
 
		public boolean remove(Object o) {
			return m.remove(o) != null;
		}
 
		public boolean add(E e) {
			return m.put(e, Boolean.TRUE) == null;
		}
 
		public Iterator<E> iterator() {
			return s.iterator();
		}
 
		public Object[] toArray() {
			return s.toArray();
		}
 
		public <T> T[] toArray(T[] a) {
			return s.toArray(a);
		}
 
		public String toString() {
			return s.toString();
		}
 
		public int hashCode() {
			return s.hashCode();
		}
 
		public boolean equals(Object o) {
			return o == this || s.equals(o);
		}
 
		public boolean containsAll(Collection<?> c) {
			return s.containsAll(c);
		}
 
		public boolean removeAll(Collection<?> c) {
			return s.removeAll(c);
		}
 
		public boolean retainAll(Collection<?> c) {
			return s.retainAll(c);
		}
		// addAll is the only inherited implementation
 
		private static final long serialVersionUID = 2454657854757543876L;
 
		private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
			stream.defaultReadObject();
			s = m.keySet();
		}
	}
 
	/**
	 * 
	 * 把指定Deque包装成一个后进先出的队列 add方法对应push，remove方法对应pop等。
	 * 
	 */
	public static <T> Queue<T> asLifoQueue(Deque<T> deque) {
		return new AsLIFOQueue<T>(deque);
	}
 
	static class AsLIFOQueue<E> extends AbstractQueue<E>
        implements Queue<E>, Serializable {
private static final long serialVersionUID = 1802017725587941708L;
        private final Deque<E> q;
        AsLIFOQueue(Deque<E> q)           { this.q = q; }
        public boolean add(E e)           { q.addFirst(e); return true; }
        public boolean offer(E e)         { return q.offerFirst(e); }
        public E poll()                   { return q.pollFirst(); }
        public E remove()                 { return q.removeFirst(); }
        public E peek()                   { return q.peekFirst(); }
        public E element()                { return q.getFirst(); }
        public void clear()               {        q.clear(); }
        public int size()                 { return q.size(); }
        public boolean isEmpty()          { return q.isEmpty(); }
        public boolean contains(Object o) { return q.contains(o); }
        public boolean remove(Object o)   { return q.remove(o); }
        public Iterator<E> iterator()     { return q.iterator(); }
        public Object[] toArray()         { return q.toArray(); }
        public <T> T[] toArray(T[] a)     { return q.toArray(a); }
        public String toString()          { return q.toString(); }
public boolean containsAll(Collection<?> c) {return q.containsAll(c);}
public boolean removeAll(Collection<?> c)   {return q.removeAll(c);}
public boolean retainAll(Collection<?> c)   {return q.retainAll(c);}
// We use inherited addAll; forwarding addAll would be wrong
    }
