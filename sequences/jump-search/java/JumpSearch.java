public class JumpSearch {

    public static int jumpSearch(int[] sequence, int target, int blockSize) {
        if (blockSize < 1)
            throw new IllegalArgumentException("block size must be strictly positive.");
        int block = 1;
        int endOfBlock = -1;
        do {
            endOfBlock = Math.min(blockSize*block-1, sequence.length-1);
            System.out.println("End of block " + block + " at: " + endOfBlock + ": " + sequence[endOfBlock]);
            if (sequence[endOfBlock] == target) {
                return endOfBlock;

            } else if (sequence[endOfBlock] < target) {
                block += 1;

            } else {
                var endOfPreviousBlock = blockSize*(block-1)-1;
                return linearSearch(sequence, target, endOfPreviousBlock+1, endOfBlock);

            }
        } while (endOfBlock < sequence.length-1);
        return -1;
    }

    public static int linearSearch(int[] sequence, int target, int start, int last) {
        System.out.println("Linear search from: " + start + " to " + last);
        for(int i=start ; i<last ; i++) {
            if (sequence[i] == target) return i;
        };
        return -1;
    }


    public static int binarySearch(int[] sequence, int target) {
        int lowEnd = 0;
        int highEnd = sequence.length;
        int cut = (lowEnd + highEnd) / 2;
        while (highEnd - lowEnd >= 1) {
            if (sequence[cut] == target)
                return cut;
            else if (sequence[cut] < target)
                lowEnd = cut + 1;
            else
                highEnd = cut;
            cut = (lowEnd + highEnd) / 2;
        }
        return -1;
    }


    public static int interpolatedSearch(int[] sequence, int target) {
        int lowEnd = 0;
        int highEnd = sequence.length;
        int cut = interpolate(sequence, lowEnd, highEnd, target);
        while (highEnd - lowEnd > 1 && cut >= lowEnd && cut < highEnd) {
            if (sequence[cut] == target)
                return cut;
            else if (sequence[cut] < target)
                lowEnd = cut + 1;
            else
                highEnd = cut;
            // System.out.println("low: " + lowEnd + " ; high: " + highEnd);
            cut = interpolate(sequence, lowEnd, highEnd, target);
        }
        return -1;
    }

    public static int interpolate(int[] sequence, int low, int high, int target) {
        if (low == high) return low;
        float ratio = (target - sequence[low])
               / (sequence[high-1] - sequence[low]);
        // System.out.println("Ratio: " + ratio);
        return low + Math.round((high-1-low) * ratio);
    }

    public static void testWith(int[] sequence, int target) {
        var index = interpolatedSearch(sequence, target);
        System.out.println("Found " + target + " at: " + index);
    }

    public static void main(String[] args) {

        int[] sequence = {1, 3, 5, 7,
                          9, 11, 13, 15,
                          17, 19, 21, 23,
                          25};

        testWith(sequence, 0);
        testWith(sequence, 1);
        testWith(sequence, 3);
        testWith(sequence, 13);
        testWith(sequence, 14);
        testWith(sequence, 23);
        testWith(sequence, 25);
        testWith(sequence, 26);
    }


}
