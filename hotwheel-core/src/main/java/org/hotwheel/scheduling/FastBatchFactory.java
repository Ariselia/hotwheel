package org.hotwheel.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.concurrent.RecursiveTask;

/**
 * Created by wangfeng on 2016/11/15.
 * @since 2.0.15
 */
public abstract class FastBatchFactory<T extends PartitionContext> extends RecursiveTask<T> implements TaskContext<T>{
    protected static final String PROP_THRESHOLD = "threshold";
    protected static final String PROP_THREADNUM = "threadNum";
    protected static final String PROP_BATCHSIZE = "batchSize";

    protected static final String[] ALL_PROPERTIES = {
            PROP_THRESHOLD,
            PROP_THREADNUM,
            PROP_BATCHSIZE
    };
    /** 线程数 */
    protected volatile int numberOfThread = 0;

    protected int threadNum = Runtime.getRuntime().availableProcessors();
    protected int threshold = 1000;
    protected int batchSize = 100;

    protected Logger logger =  LoggerFactory.getLogger(getClass());
    protected String taskName = null;
    protected Class<T> contextClass;

    /** 开始行数 */
    protected int start;
    /** 结束行数 */
    protected int end;
    /** 传入参数 */
    protected Object[] args = null;

    public FastBatchFactory() {
        this(Runtime.getRuntime().availableProcessors(), 1000, 100, "default");
    }

    /**
     * 创建批量任务
     * @param threadNum
     * @param threshold
     * @param batchSize
     * @param taskName
     */
    public FastBatchFactory(int threadNum, int threshold, int batchSize, String taskName) {
        this.threadNum = threadNum;
        this.threshold = threshold;
        this.batchSize = batchSize;
        this.taskName = taskName;
    }

    /**
     * 批量任务构造函数
     *
     * @param start
     * @param end
     * @param args
     */
    public void init(int start, int end, Object... args) {
        this.start = start;
        this.end = end;
        this.args = args;
    }

    private FastBatchFactory newTask(int start, int end, Object... args) {
        FastBatchFactory task = null;
        Constructor<?> constructor = null;
        try {
            //constructor = getClass().getConstructor(new Class[] {int.class, int.class, Object[].class});
            constructor = getClass().getConstructor(new Class[] {int.class, int.class, int.class, String.class});
            if(constructor != null) {
                task = (FastBatchFactory)constructor.newInstance(this.threadNum, this.threshold, this.batchSize, this.taskName);
                task.init(start, end, args);
            }
        } catch (Exception e) {
            logger.error("create task[{}] error", taskName, e);
        }
        return task;
    }

    @Override
    protected T compute() {
        T ret = getContext();
        ret.taskName = taskName;
        //ret.file = data.file;
        //ret.fields = data.fields;
        numberOfThread++;
        logger.info(taskName + ": " + start + "->" + end + ": 1");
        //如果任务足够小就计算任务
        int remaining = (end - start);
        //boolean canCompute = (end - start) <= threshold;
        if (remaining <= 0) {
            // 不执行
            //return ret;
        } else if(remaining == 1 || remaining <= threshold) {
            logger.info(taskName + ": " + start + "->" + end + ": 2");
            execute(ret.lines);
        } else {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end) / 2;
            FastBatchFactory leftTask = newTask(start, middle, args);
            FastBatchFactory rightTask = newTask(middle, end, args);
            // 执行子任务
            leftTask.fork();
            rightTask.fork();

            //等待任务执行结束合并其结果
            PartitionContext leftResult = (T)leftTask.join();
            PartitionContext rightResult = (T)rightTask.join();

            //合并子任务
            if(leftResult.lines != null) {
                ret.lines.addAll(leftResult.lines);
            }
            if(rightResult.lines != null) {
                ret.lines.addAll(rightResult.lines);
            }
            logger.info(taskName + ": " + start + "->" + end + ": 3");
        }
        return ret;
    }
}