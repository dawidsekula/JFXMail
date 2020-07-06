package site.transcendence.model;

public class MessageSize implements Comparable<MessageSize> {

    private final long size;

    public MessageSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        String result;
        if (size <= 0){
            result = "0";
        }else if (size < 1024){
            result = size + " B";
        }else if (size < 1048576){
            result = size / 1024 + " kB";
        }else{
            result = size / 1048576 + " MB";
        }
        return result;
    }

    @Override
    public int compareTo(MessageSize o) {
        return Long.compare(size, o.size);
    }

}
