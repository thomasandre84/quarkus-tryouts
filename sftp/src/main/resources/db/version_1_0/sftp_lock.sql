create table sftp_lock (
    id INT NOT NULL AUTO_INCREMENT,
    expireTime timestamp(6),
    lockTime timestamp(6),
    lockingHost varchar(255),
    targetHost varchar(255),
    version integer,
    primary key (id)
);