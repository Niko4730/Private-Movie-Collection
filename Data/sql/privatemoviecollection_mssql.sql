/*
 Navicat Premium Data Transfer

 Source Server         : Local MSSQL
 Source Server Type    : SQL Server
 Source Server Version : 15002000
 Source Host           : localhost\SQLEXPRESS:1433
 Source Catalog        : privatemoviecollection
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 15002000
 File Encoding         : 65001

 Date: 08/01/2021 12:03:33
*/


-- ----------------------------
-- Table structure for category
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[category]') AND type IN ('U'))
	DROP TABLE [dbo].[category]
GO

CREATE TABLE [dbo].[category] (
  [category_id] int  IDENTITY(1,1) NOT NULL,
  [category_name] nvarchar(255) COLLATE Danish_Norwegian_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[category] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Table structure for category_movie
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[category_movie]') AND type IN ('U'))
	DROP TABLE [dbo].[category_movie]
GO

CREATE TABLE [dbo].[category_movie] (
  [category_id] int  NOT NULL,
  [movie_id] int  NOT NULL
)
GO

ALTER TABLE [dbo].[category_movie] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Table structure for movie
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[movie]') AND type IN ('U'))
	DROP TABLE [dbo].[movie]
GO

CREATE TABLE [dbo].[movie] (
  [movie_id] int  IDENTITY(1,1) NOT NULL,
  [movie_title] nvarchar(255) COLLATE Danish_Norwegian_CI_AS  NULL,
  [movie_filepath] nvarchar(255) COLLATE Danish_Norwegian_CI_AS  NULL,
  [movie_lastview] nvarchar(255) COLLATE Danish_Norwegian_CI_AS  NULL,
  [movie_length] float(53)  NULL,
  [movie_rating] float(53)  NULL,
  [category_id] int  NULL
)
GO

ALTER TABLE [dbo].[movie] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Auto increment value for category
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[category]', RESEED, 1)
GO


-- ----------------------------
-- Primary Key structure for table category
-- ----------------------------
ALTER TABLE [dbo].[category] ADD CONSTRAINT [PK__category__D54EE9B46EBEC81B] PRIMARY KEY CLUSTERED ([category_id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Indexes structure for table category_movie
-- ----------------------------
CREATE NONCLUSTERED INDEX [movie_id]
ON [dbo].[category_movie] (
  [movie_id] ASC
)
GO


-- ----------------------------
-- Foreign Keys structure for table category_movie
-- ----------------------------
ALTER TABLE [dbo].[category_movie] ADD CONSTRAINT [movie_collection_ibfk_1] FOREIGN KEY ([movie_id]) REFERENCES [dbo].[movie] ([movie_id]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO


-- ----------------------------
-- Auto increment value for movie
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[movie]', RESEED, 1)
GO


-- ----------------------------
-- Primary Key structure for table movie
-- ----------------------------
ALTER TABLE [dbo].[movie] ADD CONSTRAINT [PK__movie__83CDF7498C0CAA7E] PRIMARY KEY CLUSTERED ([movie_id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO

