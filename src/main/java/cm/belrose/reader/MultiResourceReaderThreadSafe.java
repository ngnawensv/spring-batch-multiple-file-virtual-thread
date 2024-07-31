package cm.belrose.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.core.io.Resource;

/**
 * This class is a thread safe class
 * MultiResourceItemReader should be executed within the thread safe
 * @param <T>
 */
@RequiredArgsConstructor
public class MultiResourceReaderThreadSafe<T> implements ItemReader<T> {


    private final MultiResourceItemReader<T> delegate;
    private final Object lock = new Object();

    @Override
    public T read() throws Exception {
        synchronized (lock){
            return delegate.read();
        }
    }

    public void setResources(Resource[] resources){
        synchronized (lock){
             delegate.setResources(resources);
        }
    }

    public void open(ExecutionContext executionContext){
        synchronized (lock){
            delegate.open(executionContext);
        }
    }

    public void close(){
        synchronized (lock){
            delegate.close();
        }
    }
}
