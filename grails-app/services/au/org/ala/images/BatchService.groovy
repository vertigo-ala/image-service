package au.org.ala.images

class BatchService {

    private final static Map<String, BatchStatus> _Batches = [:]

    def imageService

    public String createNewBatch() {
        BatchStatus status = new BatchStatus()

        synchronized (_Batches) {
            _Batches[status.batchId] = status
        }

        return status.batchId
    }

    public void addTaskToBatch(String batchId, BackgroundTask task) {

        synchronized (_Batches) {
            if (!_Batches.containsKey(batchId)) {
                throw new RuntimeException("Unknown or invalid batch id!")
            }
            def status = _Batches[batchId]
            def batchTask = new BatchBackgroundTask(batchId, status.taskCount, task, this)
            status.taskCount = status.taskCount + 1

            imageService.scheduleBackgroundTask(batchTask)
        }
    }

    public void notifyBatchTaskComplete(String batchId, int taskSequenceNumber, Object result) {

        if (!_Batches.containsKey(batchId)) {
            return
        }

        def status = _Batches[batchId]
        status.tasksCompleted = status.tasksCompleted + 1
        status.results[taskSequenceNumber] = result

        if (status.tasksCompleted == status.taskCount) {
            status.timeFinished = new Date()
        }
    }

    public BatchStatus getBatchStatus(String batchId) {

        if (!_Batches.containsKey(batchId)) {
            return null
        }

        return _Batches[batchId]
    }

    public void finaliseBatch(String batchId) {
        synchronized (_Batches) {
            if (!_Batches.containsKey(batchId)) {
                return
            }

            _Batches.remove(batchId)
        }
    }

}
