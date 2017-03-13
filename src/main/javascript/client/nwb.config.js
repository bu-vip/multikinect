const WebpackShellPlugin = require('webpack-shell-plugin');

module.exports = {
  type: 'react-app',
  karma: {
    browsers: ['Chrome']
  },
  webpack: {
    extra: {
      devtool: '#inline-source-map',
      plugins: [
        new WebpackShellPlugin({
          onBuildStart:[
              './build_proto.sh'
          ],
          onBuildEnd:[]
        })
      ],
      module: {
        rules: [
          {
            test: /\.proto$/,
            loader: "raw-loader"
          }
        ]
      }
    }
  }
}
