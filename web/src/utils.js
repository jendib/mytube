const formatDuration = (duration) => {
  const formatted = new Date(duration * 1000).toISOString().slice(11, 19);
  if (duration < 3600) {
    return formatted.substring(3);
  }
  return formatted;
}

export default formatDuration