-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 24, 2024 at 03:20 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `inglizgo_app`
--

-- --------------------------------------------------------

--
-- Table structure for table `user_attempts`
--

CREATE TABLE `user_attempts` (
  `attempt_id` int(11) NOT NULL,
  `word_id` int(11) DEFAULT NULL,
  `UserName` varchar(50) NOT NULL,
  `correct_answers` smallint(1) DEFAULT 0,
  `attempt_date` timestamp NULL DEFAULT NULL,
  `repetition` int(11) DEFAULT 1,
  `next_review_date` timestamp NULL DEFAULT NULL,
  `total_attempts` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_info`
--

CREATE TABLE `user_info` (
  `UserName` varchar(50) NOT NULL,
  `UserEmail` varchar(50) NOT NULL,
  `UserPassword` varchar(50) NOT NULL,
  `SecurityQuestion` varchar(50) NOT NULL,
  `SecQueAnswer` varchar(50) NOT NULL,
  `User_Photo` longblob DEFAULT NULL,
  `Date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `wordcards`
--

CREATE TABLE `wordcards` (
  `word_id` int(11) NOT NULL,
  `EN_word` varchar(255) NOT NULL,
  `TR_translate` varchar(255) NOT NULL,
  `FirstEx` text DEFAULT NULL,
  `SecondEx` text DEFAULT NULL,
  `UserName` varchar(255) DEFAULT NULL,
  `Word_Image` longblob DEFAULT NULL,
  `CreatedAt` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` varchar(50) DEFAULT 'learning'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `user_attempts`
--
ALTER TABLE `user_attempts`
  ADD PRIMARY KEY (`attempt_id`),
  ADD UNIQUE KEY `word_id` (`word_id`) USING BTREE,
  ADD KEY `fk_UserName_userAttemtps` (`UserName`);

--
-- Indexes for table `user_info`
--
ALTER TABLE `user_info`
  ADD PRIMARY KEY (`UserName`);

--
-- Indexes for table `wordcards`
--
ALTER TABLE `wordcards`
  ADD PRIMARY KEY (`word_id`),
  ADD KEY `fk_UserName` (`UserName`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `user_attempts`
--
ALTER TABLE `user_attempts`
  MODIFY `attempt_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=412;

--
-- AUTO_INCREMENT for table `wordcards`
--
ALTER TABLE `wordcards`
  MODIFY `word_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=54;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_attempts`
--
ALTER TABLE `user_attempts`
  ADD CONSTRAINT `fk_UserName_userAttemtps` FOREIGN KEY (`UserName`) REFERENCES `user_info` (`UserName`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_wordId` FOREIGN KEY (`word_id`) REFERENCES `wordcards` (`word_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `user_attempts_ibfk_2` FOREIGN KEY (`word_id`) REFERENCES `wordcards` (`word_id`),
  ADD CONSTRAINT `user_attempts_ibfk_3` FOREIGN KEY (`word_id`) REFERENCES `wordcards` (`word_id`);

--
-- Constraints for table `wordcards`
--
ALTER TABLE `wordcards`
  ADD CONSTRAINT `fk_UserName` FOREIGN KEY (`UserName`) REFERENCES `user_info` (`UserName`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
