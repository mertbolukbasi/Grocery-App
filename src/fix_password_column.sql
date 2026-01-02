-- Migration script to fix password column size issue
-- Run this script if you have an existing database with VARCHAR(50) password column

USE Group16;

-- Alter the password column to accommodate full SHA-256 hashes (64 characters)
ALTER TABLE UserInfo MODIFY COLUMN password VARCHAR(64) NOT NULL;

-- If passwords were truncated, you may need to re-insert them with correct hashes
-- The following are the correct SHA-256 hashes for the default passwords:
-- 'cust' -> '80d26609c5226268981e4a6d4ceddbc339d991841ae580e3180b56c8ade7651d'
-- 'carr' -> 'f9356b0952e5681f9bb4969078d6762f1f3f3eb9e87b80d6544103ad918f074c'
-- 'own' -> '5b3975651c3cab92d044c096dc30a1c2d9525497457472de48c51ecb363d1f4a'

-- Update existing passwords if they were truncated (only if needed)
-- UPDATE UserInfo SET password = '80d26609c5226268981e4a6d4ceddbc339d991841ae580e3180b56c8ade7651d' WHERE username = 'cust';
-- UPDATE UserInfo SET password = 'f9356b0952e5681f9bb4969078d6762f1f3f3eb9e87b80d6544103ad918f074c' WHERE username = 'carr';
-- UPDATE UserInfo SET password = '5b3975651c3cab92d044c096dc30a1c2d9525497457472de48c51ecb363d1f4a' WHERE username = 'own';

SELECT 'Password column has been updated to VARCHAR(64)' AS Status;

