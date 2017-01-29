module.exports = {
  type: 'react-app',
  karma: {
    browsers: ['Chrome']
  },
  webpack: {
    extra: {
      devtool: '#inline-source-map',
      module: {
        loaders: [
          {
            test: /\.proto$/,
            loader: "raw-loader"
          }
        ]
      }
    }
  }
}
