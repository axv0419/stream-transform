



###  Create Topics on Confluent Cloud

```bash
ccloud topic create --partitions 10 --replication-factor 3 -- test-01.stream.in 
ccloud topic create --partitions 10 --replication-factor 3 -- test-01.stream.out
ccloud topic create --partitions 10 --replication-factor 3 -- test-01.stream.error 
ccloud topic create --partitions 10 --replication-factor 3 -- ajar-stream-KSTREAM-AGGREGATE-STATE-STORE-0000000003-repartition
ccloud topic create --partitions 10 --replication-factor 3 -- ajar-stream-KSTREAM-AGGREGATE-STATE-STORE-0000000003-changelog
```

