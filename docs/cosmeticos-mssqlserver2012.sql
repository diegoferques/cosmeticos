/*
Created: 21/05/2017
Modified: 21/05/2017
Model: MSSQL 2012
Database: MS SQL Server 2012
*/


-- Create tables section -------------------------------------------------

-- Table Professional

CREATE TABLE [Professional]
(
 [idProfessional] Bigint IDENTITY NOT NULL,
 [nameProfessional] Varchar(50) NOT NULL,
 [cpf/cnpj] Varchar(20) NOT NULL,
 [genre] Char(1) NOT NULL,
 [birth_date] Date NOT NULL,
 [cellPhone] Varchar(20) NOT NULL,
 [specialization] Varchar(20) NOT NULL,
 [type_service] Varchar(20) NOT NULL,
 [date_register] Datetime2 NOT NULL,
 [idLogin] Bigint NOT NULL,
 [idAddress] Bigint NOT NULL
)
go

-- Create indexes for table Professional

CREATE INDEX [IX_Relationship2] ON [Professional] ([idLogin])
go

CREATE INDEX [IX_Relationship4] ON [Professional] ([idAddress])
go

-- Add keys for table Professional

ALTER TABLE [Professional] ADD CONSTRAINT [Key5] PRIMARY KEY ([idProfessional])
go

ALTER TABLE [Professional] ADD CONSTRAINT [cnpj/cpf] UNIQUE NONCLUSTERED ([cpf/cnpj])
go

-- Table Log

CREATE TABLE [Log]
(
 [idLog] Bigint IDENTITY NOT NULL,
 [customerEmail] Varchar(30) NULL,
 [serviceType] Varchar(20) NULL,
 [professionalEmail] Varchar(30) NULL,
 [event] Smallint NOT NULL,
 [date] Datetime2 NOT NULL
)
go

-- Add keys for table Log

ALTER TABLE [Log] ADD CONSTRAINT [Key9] PRIMARY KEY ([idLog])
go

-- Table Customer

CREATE TABLE [Customer]
(
 [idCustomer] Bigint IDENTITY NOT NULL,
 [nameCustomer] Varchar(50) NOT NULL,
 [cpf] Varchar(15) NOT NULL,
 [genre] Char(1) NOT NULL,
 [birth_date] Date NOT NULL,
 [cellPhone] Varchar(20) NOT NULL,
 [date_register] Datetime2 NOT NULL,
 [status] Smallint NOT NULL,
 [idLogin] Bigint NOT NULL,
 [idAddress] Bigint NOT NULL
)
go

-- Create indexes for table Customer

CREATE INDEX [IX_Relationship1] ON [Customer] ([idLogin])
go

CREATE INDEX [IX_Relationship3] ON [Customer] ([idAddress])
go

-- Add keys for table Customer

ALTER TABLE [Customer] ADD CONSTRAINT [Key2] PRIMARY KEY ([idCustomer])
go

ALTER TABLE [Customer] ADD CONSTRAINT [Custumer_cpf_cnpj] UNIQUE NONCLUSTERED ([cpf])
go

-- Table Role

CREATE TABLE [Role]
(
 [idRole] Bigint IDENTITY NOT NULL,
 [name] Varchar(20) NULL
)
go

-- Add keys for table Role

ALTER TABLE [Role] ADD CONSTRAINT [Key8] PRIMARY KEY ([idRole])
go

-- Table Login

CREATE TABLE [Login]
(
 [id1] Int IDENTITY NOT NULL
)
go

-- Add keys for table Login

ALTER TABLE [Login] ADD CONSTRAINT [Key1] PRIMARY KEY ([id1])
go

-- Table Address

CREATE TABLE [Address]
(
 [idAddress] Bigint IDENTITY NOT NULL,
 [address] Varchar(128) NOT NULL,
 [cep] Varchar(20) NOT NULL,
 [neighborhood] Varchar(50) NOT NULL,
 [city] Varchar(20) NOT NULL,
 [state] Varchar(20) NOT NULL,
 [country] Varchar(20) NOT NULL
)
go

-- Add keys for table Address

ALTER TABLE [Address] ADD CONSTRAINT [Key3] PRIMARY KEY ([idAddress])
go

-- Table Location

CREATE TABLE [Location]
(
 [id] Bigint IDENTITY NOT NULL,
 [coordinate] Varchar(15) NOT NULL
)
go

-- Add keys for table Location

ALTER TABLE [Location] ADD CONSTRAINT [Key4] PRIMARY KEY ([id])
go

-- Table Service

CREATE TABLE [Service]
(
 [idService] Bigint IDENTITY NOT NULL,
 [category] Varchar(20) NOT NULL
)
go

-- Add keys for table Service

ALTER TABLE [Service] ADD CONSTRAINT [Key6] PRIMARY KEY ([idService])
go

-- Table ServiceRequest

CREATE TABLE [ServiceRequest]
(
 [idServiceRequest] Bigint IDENTITY NOT NULL,
 [date] Date NOT NULL,
 [status] Smallint NOT NULL,
 [idCustomer] Bigint NOT NULL,
 [idLocation] Bigint NOT NULL,
 [idProfessional] Bigint NOT NULL,
 [idService] Bigint NOT NULL,
 [scheduleId] Bigint NULL
)
go

-- Create indexes for table ServiceRequest

CREATE INDEX [IX_Relationship13] ON [ServiceRequest] ([idCustomer])
go

CREATE INDEX [IX_Relationship15] ON [ServiceRequest] ([idLocation])
go

CREATE INDEX [IX_Relationship19] ON [ServiceRequest] ([idProfessional],[idService])
go

CREATE INDEX [IX_Relationship28] ON [ServiceRequest] ([scheduleId])
go

-- Add keys for table ServiceRequest

ALTER TABLE [ServiceRequest] ADD CONSTRAINT [Key7] PRIMARY KEY ([idServiceRequest])
go

-- Table User

CREATE TABLE [User]
(
 [idLogin] Bigint IDENTITY NOT NULL,
 [username] Varchar(50) NOT NULL,
 [password] Varchar(20) NOT NULL,
 [email] Varchar(30) NOT NULL,
 [sourceApp] Varchar(30) NULL
)
go

-- Add keys for table User

ALTER TABLE [User] ADD CONSTRAINT [Key10] PRIMARY KEY ([idLogin])
go

ALTER TABLE [User] ADD CONSTRAINT [email] UNIQUE NONCLUSTERED ([email])
go

ALTER TABLE [User] ADD CONSTRAINT [username] UNIQUE NONCLUSTERED ([username])
go

-- Table Schedule

CREATE TABLE [Schedule]
(
 [scheduleId] Bigint NOT NULL,
 [scheduleDate] Datetime2 NULL,
 [status] Int NULL
)
go

-- Add keys for table Schedule

ALTER TABLE [Schedule] ADD CONSTRAINT [scheduleId] PRIMARY KEY ([scheduleId])
go

-- Table ProfessionalServices

CREATE TABLE [ProfessionalServices]
(
 [idProfessional] Bigint NOT NULL,
 [idService] Bigint NOT NULL
)
go

-- Add keys for table ProfessionalServices

ALTER TABLE [ProfessionalServices] ADD CONSTRAINT [Key11] PRIMARY KEY ([idProfessional],[idService])
go

-- Table UserRoles

CREATE TABLE [UserRoles]
(
 [idRole] Bigint NOT NULL,
 [idLogin] Bigint NOT NULL
)
go

-- Add keys for table UserRoles

ALTER TABLE [UserRoles] ADD CONSTRAINT [Key12] PRIMARY KEY ([idRole],[idLogin])
go



-- Create relationships section ------------------------------------------------- 

ALTER TABLE [Customer] ADD CONSTRAINT [customer_has_user] FOREIGN KEY ([idLogin]) REFERENCES [User] ([idLogin]) ON UPDATE CASCADE ON DELETE NO ACTION
go

ALTER TABLE [Professional] ADD CONSTRAINT [professional_has_user] FOREIGN KEY ([idLogin]) REFERENCES [User] ([idLogin]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [Customer] ADD CONSTRAINT [has] FOREIGN KEY ([idAddress]) REFERENCES [Address] ([idAddress]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [Professional] ADD CONSTRAINT [professional_has_address] FOREIGN KEY ([idAddress]) REFERENCES [Address] ([idAddress]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [ServiceRequest] ADD CONSTRAINT [does] FOREIGN KEY ([idCustomer]) REFERENCES [Customer] ([idCustomer]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [ServiceRequest] ADD CONSTRAINT [Relationship15] FOREIGN KEY ([idLocation]) REFERENCES [Location] ([id]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [ProfessionalServices] ADD CONSTRAINT [Relationship17] FOREIGN KEY ([idProfessional]) REFERENCES [Professional] ([idProfessional]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [ProfessionalServices] ADD CONSTRAINT [Relationship18] FOREIGN KEY ([idService]) REFERENCES [Service] ([idService]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [ServiceRequest] ADD CONSTRAINT [Relationship19] FOREIGN KEY ([idProfessional], [idService]) REFERENCES [ProfessionalServices] ([idProfessional], [idService]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [UserRoles] ADD CONSTRAINT [Relationship21] FOREIGN KEY ([idRole]) REFERENCES [Role] ([idRole]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [UserRoles] ADD CONSTRAINT [Relationship22] FOREIGN KEY ([idLogin]) REFERENCES [User] ([idLogin]) ON UPDATE NO ACTION ON DELETE NO ACTION
go

ALTER TABLE [ServiceRequest] ADD CONSTRAINT [Relationship28] FOREIGN KEY ([scheduleId]) REFERENCES [Schedule] ([scheduleId]) ON UPDATE NO ACTION ON DELETE NO ACTION
go


