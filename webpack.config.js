const Path = require('path')
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const SriPlugin = require('webpack-subresource-integrity');
const TerserPlugin = require('terser-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

module.exports = (env, argv) => ({
    entry: './src/main/front/index.jsx',
    output: {
        path: Path.resolve('./src/main/resources/static/'),
        filename: argv.mode === 'production' ? 'js/bundle.[contenthash].js' : 'js/bundle.js'
    },
    resolve: {
        modules: [
            Path.resolve('./node_modules'),
            Path.resolve('./src/main/front')
        ]
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /(node_modules)/,
                use: 'babel-loader'
            },
            {
                test: /\.css$/,
                use: [
                    argv.mode !== 'production' ? 'style-loader' : MiniCssExtractPlugin.loader,
                    'css-loader'
                ]
            },
            {
                test: /favicon\.(gif|png|jpe?g|ico|svg)$/,
                use: {
                    loader: 'file-loader',
                    options: {
                        name: '/[name].[ext]',
                    }
                }
            },
            {
                test: /(?<!favicon)\.(gif|png|jpe?g|ico|svg)$/,
                use: {
                    loader: 'file-loader',
                    options: {
                        name: '/img/[name].[ext]',
                    }
                }
            },
            {
                test: /\.(woff2?|eot|ttf)$/,
                use: {
                    loader: 'file-loader',
                    options: {
                        name: '/font/[name].[ext]',
                    }
                }
            }
        ]
    },
    optimization: {
        minimizer: [
            new TerserPlugin({
                terserOptions: {
                    ecma: 8,
                    output: {
                        comments: false
                    }
                },
                extractComments: false
            }),
            new OptimizeCSSAssetsPlugin({
                cssProcessor: require('cssnano'),
                cssProcessorPluginOptions: {
                    preset: [
                        'default',
                        {
                            discardComments: {
                                removeAll: true
                            }
                        }
                    ]
                }
            })
        ]
    },
    plugins: [
        new CleanWebpackPlugin(),
        new HtmlWebpackPlugin({
            template: './src/main/front/index.html',
            filename: 'index.html',
            minify: argv.mode === 'production' ? {
                collapseWhitespace: true,
                removeAttributeQuotes: true,
                removeComments: true
            } : false
        }),
        new SriPlugin({
            hashFuncNames: ['sha384'],
            enabled: argv.mode === 'production'
        }),
        new MiniCssExtractPlugin({
            filename: argv.mode === 'production' ? 'css/bundle.[contenthash].css' : 'css/bundle.css'
        })
    ]
});
