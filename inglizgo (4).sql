-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:4306
-- Generation Time: May 18, 2024 at 12:39 PM
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
-- Database: `inglizgo`
--

-- --------------------------------------------------------

--
-- Table structure for table `user_attempts`
--

CREATE TABLE `user_attempts` (
  `attempt_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `word_id` int(11) DEFAULT NULL,
  `correct` tinyint(1) DEFAULT NULL,
  `attempt_date` datetime DEFAULT NULL,
  `repetition` int(11) DEFAULT 1,
  `next_review_date` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_info`
--

CREATE TABLE `user_info` (
  `userId` int(11) NOT NULL,
  `UserName` varchar(50) NOT NULL,
  `UserEmail` varchar(50) NOT NULL,
  `UserPassword` varchar(50) NOT NULL,
  `SecurityQuestion` varchar(50) NOT NULL,
  `SecQueAnswer` varchar(50) NOT NULL,
  `User_Photo` longblob DEFAULT NULL,
  `Date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_info`
--

INSERT INTO `user_info` (`userId`, `UserName`, `UserEmail`, `UserPassword`, `SecurityQuestion`, `SecQueAnswer`, `User_Photo`, `Date`) VALUES
(1, 'Admin', 'Admin@gmail.com', 'Admin1234', 'What is your favorite color?', 'pink', NULL, '2024-05-13 23:01:25');

-- --------------------------------------------------------

--
-- Table structure for table `wordcards`
--

CREATE TABLE `wordcards` (
  `wordId` int(11) NOT NULL,
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
-- Dumping data for table `wordcards`
--

INSERT INTO `wordcards` (`wordId`, `EN_word`, `TR_translate`, `FirstEx`, `SecondEx`, `UserName`, `Word_Image`, `CreatedAt`, `status`) VALUES
(1, 'Hello', 'Merhaba', 'merhaba ifrah', 'Hello my dear', 'Ifrah', NULL, '2024-05-14 09:52:27', 'learning');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `user_attempts`
--
ALTER TABLE `user_attempts`
  ADD PRIMARY KEY (`attempt_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `word_id` (`word_id`);

--
-- Indexes for table `user_info`
--
ALTER TABLE `user_info`
  ADD PRIMARY KEY (`userId`);

--
-- Indexes for table `wordcards`
--
ALTER TABLE `wordcards`
  ADD PRIMARY KEY (`wordId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `user_attempts`
--
ALTER TABLE `user_attempts`
  MODIFY `attempt_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_info`
--
ALTER TABLE `user_info`
  MODIFY `userId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `wordcards`
--
ALTER TABLE `wordcards`
  MODIFY `wordId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_attempts`
--
ALTER TABLE `user_attempts`
  ADD CONSTRAINT `user_attempts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`userId`),
  ADD CONSTRAINT `user_attempts_ibfk_2` FOREIGN KEY (`word_id`) REFERENCES `wordcards` (`wordId`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
