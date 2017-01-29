import {connect} from 'react-redux';

import FeedView from './FeedView';

const mapStateToProps =
    (state, ownProps) => { return {}; };

const mapDispatchToProps = (dispatch) => { return {}; };

const FeedPage =
    connect(mapStateToProps, mapDispatchToProps)(FeedView);

const baseUrl = '/feed';
FeedPage.route = baseUrl;
FeedPage.url = () => baseUrl;

export default FeedPage;
