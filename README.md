# Ghi chú cho bản thân

## Agenda

### What is Kafka?

Apache Kafka là một nền tảng **phân phối luồng sự kiện** mã nguồn mở (_open source **distributed event streaming** platform_)

  - Khi nói về **event streaming**, thì bao gồm hai công việc: 

    - Tạo real-time event stream. 
    - Xử lí real-time event stream.
    
    Để hiểu rõ hơn, ta lấy ví dụ sau: *Ứng dụng đặt mua vé máy bay sử dụng API để xử lí transaction giao dịch là PayTM.*
    <br>
    > Mỗi transaction giao dịch sẽ tạo ra một **event** rồi gửi đến Kafka server.
    >- Trên ứng dụng thực tế, các transaction giao dịch có thể diễn ra đồng thời với số lượng rất lớn trong vài giây, việc này tạo ra một loạt các event gửi đến Kafka, quá trình này gọi là **Create / Generate realtime event stream of data**. 
    Khi Kafka server nhận được data, nó cần phải xử lí. 
    >- Giả sử ứng dụng có một *client application* có vai trò là đọc data từ Kafka và xử lí yêu cầu đặt vé máy bay và client application muốn hạn chế mỗi user chỉ được phép thực hiện 10 transaction mỗi ngày. Nếu vượt mức transaction, application sẽ **send một mail thông báo** đến email của user.
    >- Trong trường hợp như trên, client application phải **liên tục** thực hiện **validation kiểm tra số lượng transaction của mỗi user**. Nghĩa là application phải **liên tục lắng nghe** Kafka Server để nhận message. Quá trình này được gọi là **Processing realtime event stream of data**.
  - Khi nói về **distributed**, thì trong **Microservices**, định nghĩa distributed nghĩa là phân tán nhiều máy chủ đến nhiều node khác nhau hay nhiều region khác nhau để cân bằng tải và tránh down time:
    >- Kafka là distributed platform, nghĩa là ta cũng có thể phân tán Kafka server chạy ở nhiều region khác nhau. 
    >- Trong trường hợp có một server nào đó bị sập, server khác sẽ thay thế nhận lấy traffic để tránh trường hợp cả hệ thống bị sập.

### Why do we need Kafka?

#### Ví dụ đơn giản:
Service **A** có một message quan trọng cần phải nhận vì message này liên quan đến logic của toàn hệ thống hay liên quan đến giao dịch tiền. Thế nhưng trong lúc message gửi đến thì **A** đang bị sập và không thể nhận được, dẫn đến mất mát dữ liệu trong hệ thống hay tệ hơn là sai logic của toàn bộ hệ thống.

Trong trường hợp này, Kafka đóng vai trò như là một **hòm đưa thư**, khi message đến service, message sẽ được đặt vào hòm thư này và khi hệ thống hoạt động trở lại, chỉ việc lấy tất cả message từ hòm thư và xử lí đồng loạt.

#### Ví dụ phức tạp hơn
Giả sử chúng ta có 4 service cần giao tiếp và kết nối với 5 service/server khác nhau
![image](images/Screenshot%202024-08-11%20155850.png)

Trong trường hợp này, việc quản lí kết nối giữa các service sẽ là một bài toán khó khăn bởi vì chúng ta cần phải quan tâm đến các yếu tố sau đây đối với mỗi service:
1. Data Format
2. Connection Type
3. Số lượng Connection
##### 1. Data Format
Mỗi service có thể muốn cung cấp hoặc nhận một loại data theo format khác nhau. Rất khó khăn để handle data format và schema cho mỗi service trong trường hợp này.
##### 2. Connection Type
Có thể có nhiều loại kết nối (HTTP, TCP, JDBC, ...), với nhiều loại connect này sẽ rất khó để maintain giữa các service.
##### 3. Số lượng Connection
Nếu chúng ta nhìn nhận kĩ càng sẽ dễ dàng nhận thấy mỗi service ở sơ đồ trên hiện tại đang duy trì 5 connection đến các server đích. Tổng cộng là **20 connection**.

![image](images/Screenshot%202024-08-11%20174806.png)

Với nhiều kết nối và phân tán ra như vậy, việc quản lý rất khó khăn. Kafka sẽ giải quyết vấn đề bằng cách tập trung chúng lại một chỗ như sau:

![image](images/Screenshot%202024-08-11%20175359.png)

Xem xét sơ đồ:
- Mỗi service giờ đây sẽ không quan tâm đến data format, chúng chỉ đơn giản là gửi dữ liệu tập trung đến Kafka server.
- Server đích cần loại data nào, sẽ chủ động lấy từ Kafka ra.
- Số lượng connection giảm đi, giờ đây chỉ còn **9 connection**.

### How does Kafka work (high level overview)
### Khái quát đơn gản về mô hình Pub/Sub
Bao gồm 3 phần:
1. Publisher
2. Subsciber
3. Message Broker
- **Publisher** là bên sẽ "phát hành" event hoặc message đến hệ thống Kafka.
- Message sẽ đc gửi đi rồi lưu trữ tại **Message Broker** hay nói cách khác là **Kafka Server**.
- **Subsribers** sẽ đi đến các **Message Broker** cụ thể và yêu cầu Message, hoặc các Subsribers cơ bản sẽ **lắng nghe** các broker để lấy message.

![image](images/image.png)

## Kafka Architecture & Components
### Cluster
Cluster là một khái niệm quen thuộc trong thế giới Microservice, chúng là một cụm các máy chủ. Trong định nghĩa của Kafka cũng tương tự.
Bởi vì Kafka cũng là hệ thống phân tán, nó có thể có nhiều **Kafka server** hay **Broker** trong một **Kafka Cluster**
>- Có thể có một hoặc nhiều broker trong một Kafka cluster.
>- Cluster giúp đảm bảo khả năng chịu lỗi, khả năng mở rộng và hiệu suất cao trong việc xử lý một lượng lớn dữ liệu event.

![image](images/Screenshot%202024-08-11%20182401.png)

### Topic
Topic định nghĩa danh mục cho các message hoặc dán nhãn cho các message và từ đó, Consumer có thể nhận được các message liên quan đến Topic mà mình đã đăng ký lắng nghe.

Xem xét lại ví dụ PayTM trước đó:

![image](images/Screenshot%202024-08-11%20183821.png)

- **Consumer** - *client application* yêu cầu Kafka gửi tất cả message (các transaction) cho mình.
- **Broker** - *Kafka* gửi toàn bộ message bên trong đến cho application, dẫn đến có nhiều message không liên quan.

![image](images/Screenshot%202024-08-11%20184112.png)

- **Consumer** - *client application* thay vào đó, yêu cầu gửi tất cả message payment liên quan cho mình.
- **Broker** - *Kafka* lại tiếp tục confuse vì có nhiều loại message bên trong nó, và không biết làm sao để lọc ra các message liên quan đến payment.

=> lúc này chúng ta cần đến **Topic**.

![image](images/Screenshot%202024-08-11%20185012.png)

- Với **Topic**, Broker có thể phân nhóm các message theo từng Topic khác nhau, và **Consumer** giờ đây chỉ cần lắng nghe một Topic cụ thể để nhận message liên quan
- Chúng ta có thể xem Topic giống như là các bảng trong database của Kafka Server, với mỗi message, Kafka sẽ kiểm tra xem nên thêm message đó vào bảng nào trong database.

![images](images/Screenshot%202024-08-11%20185355.png)

### Partitions

Chúng ta đã biết PayTM sẽ sản sinh ra rất nhiều message đến Broker và Broker có thể lưu các message nào theo từng Topic.

Giả sử lúc này đây, chúng ta có số lượng **cực lớn** các message, lên đến hàng triệu. Lúc này đây topic là không đủ để handle các message.

Việc lưu các message trở nên bất khả thi trên một máy chủ. Do kafka là một hệ thốntg máy chủ phân tán, thế nên chúng ta có thể **chia nhỏ** các Topic thành nhiều phần, và phân tán mỗi phần sang một máy chủ khác.

Các phần topic được gọi là các **Partitions**

![image](images/Screenshot%202024-08-11%20190745.png)

Với việc có nhiều Partiton cho mỗi Topic, mỗi khi publisher gửi một lượng lớn message cho một topic nhất định, thì mỗi Partition trong topic chỉ việc lưu một phần message, giúp cải thiện đáng kể hiệu năng.

Và kể cả khi có một partition bị down, miễn là các partition khác còn hoạt động thì cả kênh giao tiếp sẽ không bị gián đoạn.

### Offset

Đến giờ chúng ta đã nắm được:

- Mỗi kafka cluster sẽ có nhiều kafka server.
- Mỗi kafka server sẽ có nhiều kafka topic.
- Mỗi Kafka topic sẽ có nhiều partions.

Mỗi khi producer gửi message đến, message này sẽ nằm bên trong **bất kì partition nào** của một topic nhất định.

Chúng ta sẽ không kiểm soát quá trình này, quá trình này hoạt động dựa trên quy tắc xoay vòng. Mỗi khi một message được đặt vào một partion, nó sẽ được gắn một số để định danh vị trí được gọi là **offset**. Các số này là một dãy liên tục tăng.

Vai trò của **offset** là sẽ giúp chúng ta biết được **message nào đã được consume** bởi consumer.

![image](images/Screenshot%202024-08-11%20192024.png)

Lấy ví dụ: 

>- Giả sử trong quá trình hoạt động, Consumer đã consume được đến message có offset 3 thì bỗng dưng Consumer vì lí do gì đó lại offline.
>- Trong quá trình offline, có thêm 2 message offset 4 và 5 được gửi đến.
>![image](images/Screenshot%202024-08-11%20192248.png)
>- Khi consumer online trở lại, số offset 3 sẽ giúp Broker biết được nên gửi tiếp message offset 4 và 5 cho consumer.
>![image](images/Screenshot%202024-08-11%20192634.png)

### Consumer Groups
Đến hiện tại chúng ta đã rõ các khái niệm này:
1. Producer sẽ push một lượng lớn các message vào một **Topic**.
2. Các Message lại được chia nhỏ vào nhiều **Partition** khác nhau của một **Topic**.
3. Bên trong mỗi **Partiton**, các message sẽ có được đánh dấu một số **Offset**.

Với kiến trúc hiện tại, mỗi một consumer phải lắng nghe **tất cả partition** để đảm bảo lấy được message cần thiết. Điều này là không tối ưu vì không có tính tuần tự.

![image](images/Screenshot%202024-08-11%20195230.png)

Giải quyết vấn đề này, chúng ta sẽ **chia nhỏ workload**, cụ thể là:

- Xác định n instance consumer.
- Nhóm toàn bộ consumer này vào một đơn vị khác có tên *payment_consumer_group*.
- Với nhiều consumer, chúng ta có thể chia workload cho mỗi consumer để có được hiệu suất throughput tốt hơn.
![image](images/Screenshot%202024-08-11%20195542.png)
>- Với cách tiếp cận này, cả 3 consumer sẽ chỉ cần lắng nghe một partition.

_**Lưu ý:** Chúng ta không thể đảm bảo thứ tự của consumer và partion, bất kì consumer nào cũng có thể lắng nghe đến bất kì partiton nào. Việc này sẽ được quyết định bởi Coordinator_

Chúng ta đã có 3 consumer lắng nghe đến 3 partion khác nhau, vậy trong trường hợp có consumer thứ 4?

![image](images/Screenshot%202024-08-11%20200154.png)

**Không thay đổi!** Consumer thứ 4 sẽ idle bởi vì tất cả các partion đều đã được lắng nghe và không còn partiton nào cho Consumer này. Nhưng trong trường hợp có bất kì Consumer nào reject hoặc offline, thì Consumer thứ 4 sẽ có cơ hội connect.

Khái niệm này được gọi là **Consumer Rebalancing**.

### Zookeeper (cũ)
Zookeeper là điều kiện tiên quyết của Kafka, Kafka là hệ thống phân tán và nó sử dụng Zookeeper để quản lí metadata bao gồm topic, partiton, offset, vv... và theo dõi tình trạng của một Kafka Server.

![image](images/Screenshot%202024-08-11%20203359.png)

Hay nói một cách đơn giản dễ hiểu, Zookeeper sẽ là một người quản lí Kafka Cluster của chúng ta.

Apache Kafka đã chuyển từ việc phụ thuộc vào Zookeeper sang một kiến trúc mới gọi là **KRaft** (Kafka Raft Metadata Mode), nhưng Zookeeper vẫn được sử dụng trong nhiều cài đặt Kafka hiện tại.

### KRaft (mới)
Kể từ Apache Kafka phiên bản 2.8 trở đi, chúng ta có thể sử dụng Kafka mà không cần đến **ZooKeeper**. 

Trước đây chúng ta phải lưu giữ metadata của Kafka Cluster bên trong ZooKeeper, mà giờ đây chúng ta có thể lưu trữ các metadata này bên trong chính Kafka Topic Partition.

Để sử dụng Kafka mà không cần ZooKeeper, chúng ta sẽ chạy Kafka ở **KRaft mode**.