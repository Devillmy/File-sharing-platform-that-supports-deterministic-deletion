# File-sharing-platform-that-supports-deterministic-deletion
  近年来，随着使用云存储服务的个人与企业数量不断增长，用户敏感数据的泄露成为一个重要的安全问题。当用户将数据外包至云进行存储时，就失去了对数据的直接控制，当数据拥有者想要删除数据时，可能存在数据删除后仍会被访问的问题，以及数据分享过程中存在授权不灵活问题。针对以上问题，设计并实现了支持数据确定性删除的数据安全共享平台，同时实现了支持细粒度的数据确定性删除及灵活的访问控制。
  平台包含数据拥有者DO、数据请求者DR、云存储服务提供商CSP和可信第三方密钥管理中心TTKMC四个实体。每个数据拥有者拥有唯一根密钥RK，当上传某个数据前生成对应的数据加密密钥 DEK ，使用该DEK加密数据后，将生成的密文数据ED上传到CSP，同时用RK加密DEK生成再加密密钥REK，并将REK上传至TTKMC后在DO本地销毁DEK；当需要删除数据时，请求删除CSP中ED及TTKMC中对应的REK。由于数据是加密的，并且只有再加密密钥是唯一可能被窃取的，因此平台可实现数据的确定性删除。为了实现灵活授予访问控制权限，DR通过DO提供的Token向CSP请求数据，经CSP验证Token后将数据提供给DR。平台实现时，针对密钥生成、传输、存储以及数据加密过程中可能存在的安全风险，利用SGX技术保证根密钥安全存储、关键计算环节的运行安全以及节点之间的安全通信。
  不同于一般基于密码学的云存储数据确定性删除方案，本平台实现了一次数据加密密钥加密数据，不需要对数据加密密钥二次或多次加密，提出了一个新的支持可确定性删除的数据共享方案。该方案设计了去中心化的访问控制，能够更加灵活的将数据分享给可信任的用户，数据加密使用国密算法SM4。基于该方案实现了支持可确定性删除的数据安全共享平台原型，证明了方案的可行性。在对本作品所提方案引入的安全机制进行安全性分析后，可以得到该平台能够安全且可靠地完成云存储数据的确定性删除的结果。

平台暂时实现了：SGX中使用SM4加密解密文件、SGX中生成密钥、前端文件管理系统、DO、DR，暂时未将前后端接口连接在一起
