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
