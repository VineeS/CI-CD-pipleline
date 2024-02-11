from pyspark.sql import SparkSession

def lambda_handler(event, context):
    # Initialize SparkSession
    spark = SparkSession.builder \
    .appName("MyLambdaSparkApp") \
        .config("spark.executor.memory", "1g") \
        .config("spark.executor.cores", "1") \
        .config("spark.driver.memory", "1g") \
        .config("spark.driver.maxResultSize", "1g") \
        .config("spark.executor.instances", "1") \
        .config("spark.executor.extraJavaOptions", "-XX:+UseG1GC") \
        .config("spark.executorEnv.PYTHONHASHSEED", "0") \
        .config("spark.dynamicAllocation.enabled", "false") \
        .config("spark.sql.shuffle.partitions", "10") \
        .getOrCreate()

    # Read input data from S3 (example)
    input_data = spark.read.csv("s3://your-bucket/input-data.csv", header=True)

    # Perform data processing (example)
    processed_data = input_data.select("column1", "column2").filter("column1 > 10")

    # Write output data to S3 (example)
    processed_data.write.csv("s3://your-bucket/output-data/", mode="overwrite")

    # Stop SparkSession
    spark.stop()

    return {
    'statusCode': 200,
    'body': 'PySpark job completed successfully!'}